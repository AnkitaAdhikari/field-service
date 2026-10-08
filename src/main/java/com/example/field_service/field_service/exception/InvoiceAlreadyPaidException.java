package com.example.field_service.field_service.exception;

public class InvoiceAlreadyPaidException extends RuntimeException {
    public InvoiceAlreadyPaidException(String message) {
        super(message);
    }
}