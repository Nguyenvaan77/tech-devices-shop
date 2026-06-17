package com.example.web.event;

import java.time.LocalDateTime;

public record OrderExpiredEvent(
        Long orderId,
        String orderCode,
        Long userId,
        LocalDateTime expiredAt
) {}
