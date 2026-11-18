package com.example.web.dto.order;

import com.example.web.util.IdempotencyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {
    private IdempotencyStatus status;
    private Long orderId;
    private LocalDateTime createdAt;
}
