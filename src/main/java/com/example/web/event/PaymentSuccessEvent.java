package com.example.web.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSuccessEvent(
        Long orderId,
        String orderCode,
        Long userId,
        String email,
        String customerName,
        BigDecimal totalAmount,
        String paymentMethod,
        LocalDateTime paymentTime
) {}
