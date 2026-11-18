package com.example.web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentCode;

    private String gatewayTransactionId;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    private String responseCode;

    private String bankCode;

    private LocalDateTime transactionTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}