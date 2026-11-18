package com.example.web.dto.order.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    private Long addressId;
    private String paymentMethod;

}