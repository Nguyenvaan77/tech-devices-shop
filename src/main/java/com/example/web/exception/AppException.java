package com.example.web.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final int status;
    private final String message;

    public AppException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
