package com.example.field_service.field_service.exception;

public class PaymentAlreadyPendingException extends RuntimeException {
    public PaymentAlreadyPendingException(String message) {
        super(message);
    }
}