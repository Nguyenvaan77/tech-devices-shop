package com.example.web.service.imple;

import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.response.CartItemResponse;
import com.example.web.dto.cart.response.CartResponse;
import com.example.web.entity.Cart;
import com.example.web.entity.CartItem;
import com.example.web.entity.ProductItem;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.CartMapper;
import com.example.web.repository.CartItemRepository;
import com.example.web.repository.CartRepository;
import com.example.web.repository.ProductItemRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.AuthService;
import com.example.web.service.inter.CartService;

import ch.qos.logback.core.joran.sanity.Pair;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final ProductItemRepository productItemRepository;
        private final UserRepository userRepository;
        private final CartMapper cartMapper;

        private final AuthService authService;
        private final RedisTemplate<String, Object> redisTemplate;

        @Override
        @Transactional(readOnly = true)
        public CartResponse getCurrentCart(Long userId) {
                String cacheKey = "cart:user:" + userId;
                String fieldKey = "product:";

                /*
                cart:user:{userId} --> {
                        Key: product:{productId} 
                        Value: Quantity 
                }
                 */
                try {
                        Map<Object, Object> cacheRaw = redisTemplate.opsForHash().entries(cacheKey);
                        CartResponse cachedCart = new CartResponse();

                        if (cacheRaw != null && !cacheRaw.isEmpty()) {
                                List<CartItemResponse> cartItemResponses = new ArrayList<>();
                                for (Map.Entry<Object, Object> entry : cacheRaw.entrySet()) {
                                        String field = (String) entry.getKey();     // product:10
                                        String value = (String) entry.getValue();   // amount

                                        Long productItemId = extractProductId(field);
                                        Integer amount = Integer.parseInt(value);

                                        CartItemResponse cartItemResponse = new CartItemResponse();
                                        cartItemResponse.setProductItemId(productItemId);
                                        cartItemResponse.setQuantity(amount);

                                        cartItemResponses.add(cartItemResponse);
                                }

                                cachedCart.setItems(cartItemResponses);

                                return cachedCart;
                        }
                        
                } catch (RuntimeException exception) {
                        System.out.println("Redis unvaiable");
                }
                

                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cart not found for user: " + userId));

                CartResponse response = cartMapper.toResponse(cart);

                saveCartToRedis(userId, response);

                return response;
        }

        @Override
        public CartResponse addToCart(AddToCartRequest request) {

                // 1. Validate input
                if (request == null) {
                        throw new BadRequestException("Add to cart request cannot be null");
                }

                if (request.getProductItemId() == null || request.getProductItemId() <= 0) {
                        throw new BadRequestException("Invalid product item ID");
                }

                if (request.getQuantity() == null || request.getQuantity() <= 0) {
                        throw new BadRequestException("Quantity must be greater than 0");
                }

                // 2. Lấy user
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with email: " + email));

                // 3. Lấy cart
                Cart cart = cartRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Cart not found for user: " + user.getId()));

                // 4. Lấy product
                ProductItem productItem = productItemRepository.findById(request.getProductItemId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ProductItem not found with id: " + request.getProductItemId()));

                // 5. Kiểm tra đã tồn tại trong cart chưa
                CartItem existingItem = cartItemRepository
                        .findByCartIdAndProductItemId(cart.getId(), productItem.getId())
                        .orElse(null);

                int newQuantity;

                if (existingItem != null) {
                        // CASE 1: đã tồn tại → cộng dồn
                        newQuantity = existingItem.getQuantity() + request.getQuantity();
                        existingItem.setQuantity(newQuantity);
                        cartItemRepository.save(existingItem);
                } else {
                        // CASE 2: chưa tồn tại → tạo mới
                        newQuantity = request.getQuantity();

                        CartItem newItem = CartItem.builder()
                                .cart(cart)
                                .productItem(productItem)
                                .quantity(newQuantity)
                                .build();

                        cartItemRepository.save(newItem);
                }

                // 6. Update Redis (atomic)
                String key = "cart:user:" + user.getId();
                String field = "product:" + productItem.getId();

                redisTemplate.opsForHash().put(key, field, String.valueOf(newQuantity));

                // reset TTL
                redisTemplate.expire(key, Duration.ofMinutes(10));

                // 7. Trả response
                return cartMapper.toResponse(cart);
        }

        @Override
        public CartResponse removeFromCart(Long cartItemId) {
                if (cartItemId == null || cartItemId <= 0) {
                        throw new BadRequestException("Invalid cart item ID");
                }

                CartItem item = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "CartItem not found with id: " + cartItemId));

                Cart cart = item.getCart();
                cartItemRepository.delete(item);

                CartResponse cartResponse = cartMapper.toResponse(cart);

                // Xóa cache xong insert lại cart mới 
                removeProductItemFromCache(cart.getUser().getId(), item.getProductItem().getId());

                return cartResponse;
        }

        @Override
        public CartResponse updateCartItemQuantity(Long cartItemId, Integer quantity) {
                if (cartItemId == null || cartItemId <= 0) {
                        throw new BadRequestException("Invalid cart item ID");
                }

                if (quantity == null || quantity <= 0) {
                        throw new BadRequestException("Quantity must be greater than 0");
                }

                CartItem item = cartItemRepository.findById(cartItemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "CartItem not found with id: " + cartItemId));

                item.setQuantity(quantity);
                cartItemRepository.save(item);

                updateCartItemQuantityRedis(item.getCart().getUser().getId(), item.getProductItem().getId(), quantity);

                return cartMapper.toResponse(item.getCart());
        }

        @Override
        public void clearCart() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + email));

                Cart cart = cartRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cart not found for user: " + user.getId()));

                cartItemRepository.deleteAll(cart.getItems());

                redisTemplate.delete("cart:user:" + cart.getUser().getId());
        }

        public Long extractProductId(String field) {
                if (field == null || !field.startsWith("product:")) {
                        throw new IllegalArgumentException("Invalid field format");
                }

                try {
                        return Long.parseLong(field.substring("product:".length()));
                } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid productId");
                }
        }

        public void saveCartToRedis(Long userId, CartResponse cartResponse) {
                String key = "cart:user:" + userId;

                // Xóa dữ liệu cũ để tránh dư thừa (quan trọng)
                redisTemplate.delete(key);

                if (cartResponse == null || cartResponse.getItems() == null) {
                        return;
                }

                Map<String, String> map = new HashMap<>();

                for (CartItemResponse item : cartResponse.getItems()) {
                        String field = "product:" + item.getProductItemId();
                        String value = String.valueOf(item.getQuantity());

                        map.put(field, value);
                }

                // Ghi toàn bộ vào Redis Hash
                redisTemplate.opsForHash().putAll(key, map);

                // Set TTL nếu cần
                redisTemplate.expire(key, Duration.ofMinutes(10));
        }

        private void removeProductItemFromCache(Long userId, Long productItemId) {
                String key = "cart:user:" + userId;
                String field = "product:" + productItemId;

                redisTemplate.opsForHash().delete(key, field);
        }

        private void updateCartItemQuantityRedis(Long userId, Long productId, Integer quantity) {
                String key = "cart:user:" + userId;
                String field = "product:" + productId;

                if (quantity <= 0) {
                        redisTemplate.opsForHash().delete(key, field);
                        return;
                }

                redisTemplate.opsForHash().put(key, field, String.valueOf(quantity));
                redisTemplate.expire(key, Duration.ofMinutes(10));
        }
}
