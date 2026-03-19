package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.response.CartResponse;
import com.example.web.service.inter.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Shopping cart management APIs")
public class CartController {

        private final CartService cartService;

        @GetMapping("/{userId}")
        @Operation(summary = "Get user's cart", description = "Retrieve the shopping cart for a specific user")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid user ID"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart not found")
        })
        public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable Long userId) {
                CartResponse cart = cartService.getCart(userId);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @PostMapping("/{userId}/items")
        @Operation(summary = "Add item to cart", description = "Add a product to the shopping cart")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item added successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart or product not found")
        })
        public ResponseEntity<ApiResponse<CartResponse>> addToCart(
                        @PathVariable Long userId,
                        @RequestBody AddToCartRequest request) {
                CartResponse cart = cartService.addToCart(userId, request);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }

        @DeleteMapping("/{userId}/items/{productId}")
        @Operation(summary = "Remove item from cart", description = "Remove a product from the shopping cart")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item removed successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid user or product ID"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart or item not found")
        })
        public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
                        @PathVariable Long userId,
                        @PathVariable Long productId) {
                CartResponse cart = cartService.removeFromCart(userId, productId);
                return ResponseEntity.ok(ApiResponse.success(cart));
        }
}