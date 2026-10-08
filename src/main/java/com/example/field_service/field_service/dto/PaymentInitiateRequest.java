package com.example.field_service.field_service.dto;

import com.example.field_service.field_service.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentInitiateRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
}