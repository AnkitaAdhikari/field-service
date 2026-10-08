package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.PaymentMethod;
import com.example.field_service.field_service.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long invoiceId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private String transactionRef;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public PaymentResponse(Long id, Long invoiceId, BigDecimal amount, PaymentStatus status,
                           PaymentMethod method, String transactionRef,
                           LocalDateTime createdAt, LocalDateTime paidAt) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.transactionRef = transactionRef;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public Long getInvoiceId() { return invoiceId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public PaymentMethod getMethod() { return method; }
    public String getTransactionRef() { return transactionRef; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
}