package com.example.web.event;

import java.time.LocalDateTime;

public record PaymentFailedEvent(
        Long orderId,
        String orderCode,
        Long userId,
        String failureReason,
        LocalDateTime failureTime
) {}
