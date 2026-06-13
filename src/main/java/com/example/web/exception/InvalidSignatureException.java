package com.example.web.exception;

public class InvalidSignatureException extends PaymentException{

    public InvalidSignatureException(String message) {
        super(message);
    }
    
}
