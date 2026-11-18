package com.example.web.exception;

public class MissingIdempotencyKeyException extends BadRequestException {
    public MissingIdempotencyKeyException(String message) {
        super(message);
    }
}
