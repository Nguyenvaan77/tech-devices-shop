package com.example.web.exception;

public class OrderProcessingException extends ConflictException {
    public OrderProcessingException(String message) {
        super(message);
    }
}
