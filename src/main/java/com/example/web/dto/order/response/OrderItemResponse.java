package com.example.web.dto.order.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long productItemId;
    private String productCode;
    private Integer quantity;
    private BigDecimal price;

}