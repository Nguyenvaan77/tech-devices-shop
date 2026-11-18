package com.example.web.service.inter;

import com.example.web.dto.cart.bycache.CartByCacheResponse;
import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.request.RemoveFromCartRequest;
import com.example.web.dto.cart.response.CartResponse;

public interface CartService {
    CartByCacheResponse getCurrentCart(Long userId);

    CartByCacheResponse addToCart(AddToCartRequest request);

    CartByCacheResponse removeFromCart(RemoveFromCartRequest request);

    void clearCart();
}
