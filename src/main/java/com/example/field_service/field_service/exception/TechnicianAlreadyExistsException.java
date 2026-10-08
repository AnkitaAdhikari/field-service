package com.example.field_service.field_service.exception;

public class TechnicianAlreadyExistsException extends RuntimeException {
    public TechnicianAlreadyExistsException(String message) {
        super(message);
    }
}