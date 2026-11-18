package com.example.web.dto.cart.bycache;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartCacheDto {
    private Long userId;
    List<CartItemByCacheResponse> listCartItems;
}