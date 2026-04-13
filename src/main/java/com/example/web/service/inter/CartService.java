package com.example.web.service.inter;

import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.response.CartResponse;

public interface CartService {
    CartResponse getCurrentCart(Long userId);

    CartResponse addToCart(AddToCartRequest request);

    CartResponse removeFromCart(Long cartItemId);

    CartResponse updateCartItemQuantity(Long cartItemId, Integer quantity);

    void clearCart();
}
