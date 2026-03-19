package com.example.web.exception;

public class InternalServerException extends AppException {
    public InternalServerException(String message) {
        super(500, message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(500, message);
        initCause(cause);
    }
}
