package com.example.field_service.field_service.exception;

public class TechnicianNotFoundException extends RuntimeException {
    public TechnicianNotFoundException(String message) {
        super(message);
    }
}