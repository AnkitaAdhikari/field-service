package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private Long id;
    private Long jobId;
    private String customerName;
    private String technicianName;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime issuedAt;

    public InvoiceResponse(Long id, Long jobId, String customerName, String technicianName,
                           BigDecimal subtotal, BigDecimal taxRate, BigDecimal taxAmount,
                           BigDecimal totalAmount, InvoiceStatus status, LocalDateTime issuedAt) {
        this.id = id;
        this.jobId = jobId;
        this.customerName = customerName;
        this.technicianName = technicianName;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.status = status;
        this.issuedAt = issuedAt;
    }

    public Long getId() { return id; }
    public Long getJobId() { return jobId; }
    public String getCustomerName() { return customerName; }
    public String getTechnicianName() { return technicianName; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxRate() { return taxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public InvoiceStatus getStatus() { return status; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
}