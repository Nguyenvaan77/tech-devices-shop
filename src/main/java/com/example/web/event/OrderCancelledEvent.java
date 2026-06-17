package com.example.web.event;

import java.time.LocalDateTime;

public record OrderCancelledEvent(
        Long orderId,
        String orderCode,
        Long userId,
        String reason,
        LocalDateTime cancelledAt
) {}
