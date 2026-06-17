package com.example.web.service;

import com.example.web.event.PaymentSuccessEvent;

public interface EmailService {
    String sendPaymentSuccessEmail(PaymentSuccessEvent event) throws Exception;
}
