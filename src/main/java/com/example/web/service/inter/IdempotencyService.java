package com.example.web.service.inter;

import com.example.web.dto.order.IdempotencyRecord;

public interface IdempotencyService {
    IdempotencyRecord get(String key);
    boolean startProcessing(String key);
    void markSuccess(String key, Long orderId);
    void markFailed(String key);
    void retryProcessing(String key);
}
