package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.PaymentInitiateRequest;
import com.example.field_service.field_service.dto.PaymentResponse;
import com.example.field_service.field_service.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/invoices/{invoiceId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse initiate(@PathVariable Long invoiceId,
                                    @Valid @RequestBody PaymentInitiateRequest request) {
        return paymentService.initiate(invoiceId, request);
    }

    // TODO: replace with a signature-verified webhook when a real gateway is wired in.
    @PatchMapping("/api/payments/{id}/mark-success")
    public PaymentResponse markSuccess(@PathVariable Long id) {
        return paymentService.markSuccess(id);
    }

    @PatchMapping("/api/payments/{id}/mark-failure")
    public PaymentResponse markFailure(@PathVariable Long id) {
        return paymentService.markFailure(id);
    }

    @GetMapping("/api/payments/me")
    public List<PaymentResponse> getMyPayments() {
        return paymentService.getMyPayments();
    }
}