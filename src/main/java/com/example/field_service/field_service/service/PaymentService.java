package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.PaymentInitiateRequest;
import com.example.field_service.field_service.dto.PaymentResponse;
import com.example.field_service.field_service.entity.*;
import com.example.field_service.field_service.exception.*;
import com.example.field_service.field_service.repository.InvoiceRepository;
import com.example.field_service.field_service.repository.PaymentRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository,
                          InvoiceRepository invoiceRepository,
                          NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PaymentResponse initiate(Long invoiceId, PaymentInitiateRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found: " + invoiceId));

        String email = SecurityUtils.getCurrentUserEmail();
        if (!invoice.getCustomer().getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Only the invoiced customer can pay this invoice");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvoiceAlreadyPaidException("Invoice " + invoiceId + " is already paid");
        }

        Optional<Payment> existing = paymentRepository.findByInvoice_Id(invoiceId);

        Payment payment;
        if (existing.isPresent()) {
            payment = existing.get();
            if (payment.getStatus() == PaymentStatus.PENDING) {
                throw new PaymentAlreadyPendingException(
                        "A payment is already in progress for this invoice");
            }
            // Previous attempt FAILED — reuse the row for the retry.
            payment.setStatus(PaymentStatus.PENDING);
            payment.setMethod(request.getMethod());
            payment.setTransactionRef(generateTransactionRef());
        } else {
            payment = new Payment();
            payment.setInvoice(invoice);
            payment.setAmount(invoice.getTotalAmount());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setMethod(request.getMethod());
            payment.setTransactionRef(generateTransactionRef());
        }

        return toResponse(paymentRepository.save(payment));
    }

    // Simulates a payment gateway success callback. In production, replace
    // the controller endpoint that calls this with a webhook that verifies
    // the gateway's signature — never trust a client-supplied "it succeeded".
    @Transactional
    public PaymentResponse markSuccess(Long paymentId) {
        Payment payment = requireOwnPayment(paymentId);
        requirePending(payment);

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        Invoice invoice = payment.getInvoice();
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        notificationService.send(invoice.getCustomer().getUser(), NotificationType.PAYMENT_SUCCESS,
                "Your payment of $" + payment.getAmount() + " for invoice #" + invoice.getId() + " was successful.");
        notificationService.send(invoice.getTechnician().getUser(), NotificationType.PAYMENT_SUCCESS,
                "Payment received for invoice #" + invoice.getId() + ".");

        return toResponse(saved);
    }

    @Transactional
    public PaymentResponse markFailure(Long paymentId) {
        Payment payment = requireOwnPayment(paymentId);
        requirePending(payment);

        payment.setStatus(PaymentStatus.FAILED);
        Payment saved = paymentRepository.save(payment);

        // Invoice deliberately left UNPAID here — the customer can retry via initiate().
        notificationService.send(payment.getInvoice().getCustomer().getUser(), NotificationType.PAYMENT_FAILED,
                "Your payment for invoice #" + payment.getInvoice().getId() + " failed. Please try again.");

        return toResponse(saved);
    }

    public List<PaymentResponse> getMyPayments() {
        String email = SecurityUtils.getCurrentUserEmail();
        return paymentRepository.findByInvoice_Customer_User_EmailOrderByCreatedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    private Payment requireOwnPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));

        String email = SecurityUtils.getCurrentUserEmail();
        if (!payment.getInvoice().getCustomer().getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Only the invoiced customer can act on this payment");
        }
        return payment;
    }

    private void requirePending(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Payment is " + payment.getStatus() + ", expected PENDING");
        }
    }

    private String generateTransactionRef() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getInvoice().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getTransactionRef(),
                payment.getCreatedAt(),
                payment.getPaidAt()
        );
    }
}