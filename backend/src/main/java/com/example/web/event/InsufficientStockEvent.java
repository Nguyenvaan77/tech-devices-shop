package com.example.web.event;

public record InsufficientStockEvent(
        Long userId,
        Long productId,
        String productName,
        Integer requestedQuantity,
        Integer availableQuantity
) {}
