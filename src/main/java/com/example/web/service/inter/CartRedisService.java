package com.example.web.service.inter;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.web.dto.cart.bycache.CartByCacheResponse;
import com.example.web.dto.cart.bycache.CartItemByCacheResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CART_TTL = Duration.ofDays(30);
    private final String CART_CACHE_PREFIX = "user:cart_";

    private final String DIRTY_CARTS_KEY = "dirty:carts";

    private String getKeyFromUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id is invalid");
        }

        return CART_CACHE_PREFIX + userId;
    };

    private void addToChangedList(Long userId) {
        if(userId == null || userId <= 0) throw new IllegalArgumentException("user id is not null or less than or equal 0");

        redisTemplate.opsForSet().add(DIRTY_CARTS_KEY, userId.toString());
    }

    public java.util.Set<Long> getAllDirtyCarts() {
        java.util.Set<Object> members = redisTemplate.opsForSet().members(DIRTY_CARTS_KEY);
        if (members == null || members.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return members.stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(java.util.stream.Collectors.toSet());
    }

    public void removeDirtyCart(Long userId) {
        if (userId != null) {
            redisTemplate.opsForSet().remove(DIRTY_CARTS_KEY, userId.toString());
        }
    }



    public CartByCacheResponse getCart(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("user id is invalid");
        }

        String key = getKeyFromUserId(userId);

        Map<Object, Object> entries =
                redisTemplate.opsForHash()
                        .entries(key);

        if (entries == null || entries.isEmpty()) {
            return CartByCacheResponse.builder()
                .userId(userId)
                .listCartItems(java.util.Collections.emptyList())
                .build();
        }

        return CartByCacheResponse.builder()
                                .userId(userId)
                                .listCartItems(
                                    entries.entrySet()
                                            .stream()
                                            .map(entry ->
                                                    CartItemByCacheResponse.builder()
                                                            .product_id(
                                                                    Long.parseLong(
                                                                            entry.getKey().toString()
                                                                    )
                                                            )
                                                            .quantity(
                                                                    Integer.parseInt(
                                                                            entry.getValue().toString()
                                                                    )
                                                            )
                                                            .build()
                                            )
                                            .toList()
                                )
                                .build();

        
    } 

    public CartByCacheResponse addToCart(Long userId, Long productId, Integer quantity) {
        if (productId == null || productId  <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        String key = getKeyFromUserId(userId);

        redisTemplate.opsForHash()
                .increment(
                        key,
                        productId.toString(),
                        quantity
                );

        redisTemplate.expire(key, CART_TTL);

        addToChangedList(userId);

        return getCart(userId);
    }

    public CartByCacheResponse removeFromCart(Long userId, Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id is invalid");
        }

        String key = getKeyFromUserId(userId);

        redisTemplate.opsForHash()
                .delete(
                        key,
                        productId.toString()
                );

        redisTemplate.expire(key, CART_TTL);

        addToChangedList(userId);

        return getCart(userId);
    }

    public CartByCacheResponse removeFromCart(Long userId, Long productId, Integer quantity) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
        
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        String key = getKeyFromUserId(userId);

        Object value =
                redisTemplate.opsForHash()
                        .get(key, productId.toString());

        if(value == null) {
            throw new IllegalArgumentException(
                    "Product does not exist in cart"
            );
        }

        Integer currentQuantity = Integer.parseInt(value.toString());

        int newQuantity =
                currentQuantity - quantity;

        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    "Quantity to remove exceeds current quantity"
            );
        }

        if (newQuantity == 0) {
            redisTemplate.opsForHash()
                    .delete(
                            key,
                            productId.toString()
                    );

        } else {
            redisTemplate.opsForHash()
                    .put(
                            key,
                            productId.toString(),
                            newQuantity
                    );
    
        }

        redisTemplate.expire(key, CART_TTL);

        addToChangedList(userId);

        return getCart(userId);
    }

    public void clearCart(Long userId) {

        addToChangedList(userId);

        redisTemplate.delete(
                getKeyFromUserId(userId)
        );
    };
}
