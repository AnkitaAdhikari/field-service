package com.example.field_service.field_service.service;

import com.example.field_service.field_service.dto.InvoiceResponse;
import com.example.field_service.field_service.entity.Invoice;
import com.example.field_service.field_service.entity.InvoiceStatus;
import com.example.field_service.field_service.entity.Job;
import com.example.field_service.field_service.exception.InvoiceNotFoundException;
import com.example.field_service.field_service.exception.UnauthorizedAccessException;
import com.example.field_service.field_service.repository.InvoiceRepository;
import com.example.field_service.field_service.security.SecurityUtils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Value("${invoice.base-rate}")
    private BigDecimal baseRate;

    @Value("${invoice.tax-rate}")
    private BigDecimal taxRate;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // Called by JobService right when a job is marked COMPLETED.
    @Transactional
    public Invoice generateForJob(Job job) {
        // Flat base rate for now — swap for hourly-tracked duration or a
        // per-skill rate table once the pricing model is defined.
        BigDecimal subtotal = baseRate.setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        Invoice invoice = new Invoice();
        invoice.setJob(job);
        invoice.setCustomer(job.getServiceRequest().getCustomer());
        invoice.setTechnician(job.getTechnician());
        invoice.setSubtotal(subtotal);
        invoice.setTaxRate(taxRate);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setIssuedAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    public InvoiceResponse getById(Long id) {
        Invoice invoice = findByIdOrThrow(id);
        assertCallerCanView(invoice);
        return toResponse(invoice);
    }

    public List<InvoiceResponse> getMyInvoicesAsCustomer() {
        String email = SecurityUtils.getCurrentUserEmail();
        return invoiceRepository.findByCustomer_User_EmailOrderByIssuedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    public List<InvoiceResponse> getMyInvoicesAsTechnician() {
        String email = SecurityUtils.getCurrentUserEmail();
        return invoiceRepository.findByTechnician_User_EmailOrderByIssuedAtDesc(email)
                .stream().map(this::toResponse).toList();
    }

    public byte[] generatePdf(Long id) {
        Invoice invoice = findByIdOrThrow(id);
        assertCallerCanView(invoice);

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("Field Service Invoice", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Invoice #: " + invoice.getId(), normalFont));
            document.add(new Paragraph("Issued: " +
                    invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Customer: " + invoice.getCustomer().getUser().getName(), normalFont));
            document.add(new Paragraph("Technician: " + invoice.getTechnician().getUser().getName(), normalFont));
            document.add(new Paragraph("Job ID: " + invoice.getJob().getId(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Subtotal: $" + invoice.getSubtotal(), normalFont));
            document.add(new Paragraph("Tax (" + invoice.getTaxRate().multiply(BigDecimal.valueOf(100)) + "%): $"
                    + invoice.getTaxAmount(), normalFont));
            document.add(new Paragraph("Total Due: $" + invoice.getTotalAmount(), boldFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Status: " + invoice.getStatus(), normalFont));

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void assertCallerCanView(Invoice invoice) {
        String email = SecurityUtils.getCurrentUserEmail();
        boolean isCustomer = invoice.getCustomer().getUser().getEmail().equals(email);
        boolean isTechnician = invoice.getTechnician().getUser().getEmail().equals(email);

        if (!isCustomer && !isTechnician) {
            throw new UnauthorizedAccessException("You do not have access to this invoice");
        }
    }

    private Invoice findByIdOrThrow(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found: " + id));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getJob().getId(),
                invoice.getCustomer().getUser().getName(),
                invoice.getTechnician().getUser().getName(),
                invoice.getSubtotal(),
                invoice.getTaxRate(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                invoice.getIssuedAt()
        );
    }
}