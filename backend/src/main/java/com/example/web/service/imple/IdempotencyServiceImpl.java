package com.example.web.service.imple;

import com.example.web.dto.order.IdempotencyRecord;
import com.example.web.service.inter.IdempotencyService;
import com.example.web.util.IdempotencyStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyServiceImpl implements IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "order:";
    private static final Duration PROCESSING_TTL = Duration.ofMinutes(5);
    private static final Duration SUCCESS_TTL = Duration.ofHours(24);
    private static final Duration FAILED_TTL = Duration.ofMinutes(5);

    @Override
    public IdempotencyRecord get(String key) {
        Object obj = redisTemplate.opsForValue().get(KEY_PREFIX + key);
        if (obj == null) {
            return null;
        }
        return objectMapper.convertValue(obj, IdempotencyRecord.class);
    }

    @Override
    public boolean startProcessing(String key) {
        IdempotencyRecord record = IdempotencyRecord.builder()
                .status(IdempotencyStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                KEY_PREFIX + key,
                record,
                PROCESSING_TTL
        );

        return Boolean.TRUE.equals(success);
    }

    @Override
    public void markSuccess(String key, Long orderId) {
        IdempotencyRecord record = IdempotencyRecord.builder()
                .status(IdempotencyStatus.SUCCESS)
                .orderId(orderId)
                .createdAt(LocalDateTime.now())
                .build();

        redisTemplate.opsForValue().set(KEY_PREFIX + key, record, SUCCESS_TTL);
    }

    @Override
    public void markFailed(String key) {
        IdempotencyRecord record = IdempotencyRecord.builder()
                .status(IdempotencyStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();

        redisTemplate.opsForValue().set(KEY_PREFIX + key, record, FAILED_TTL);
    }

    @Override
    public void retryProcessing(String key) {
        IdempotencyRecord record = IdempotencyRecord.builder()
                .status(IdempotencyStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        redisTemplate.opsForValue().set(KEY_PREFIX + key, record, PROCESSING_TTL);
    }
}
