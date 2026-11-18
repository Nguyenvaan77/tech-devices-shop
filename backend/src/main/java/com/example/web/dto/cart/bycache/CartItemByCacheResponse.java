package com.example.web.dto.cart.bycache;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemByCacheResponse {
    private Long product_id;
    private Integer quantity;
}
