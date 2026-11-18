package com.example.web.dto.order.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private BigDecimal totalPrice;
    private String status;
    private List<OrderItemResponse> items;

}