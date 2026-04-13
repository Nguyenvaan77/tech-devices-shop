package com.example.web.service.imple;

import com.example.web.dto.cart.request.AddToCartRequest;
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
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

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
                CartResponse cachedCart = (CartResponse) redisTemplate.opsForValue().get(cacheKey);
                if (cachedCart != null) {
                        return cachedCart;
                }

                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cart not found for user: " + userId));

                CartResponse response = cartMapper.toResponse(cart);
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(10));
                return response;
        }

        @Override
        public CartResponse addToCart(AddToCartRequest request) {
                if (request == null) {
                        throw new BadRequestException("Add to cart request cannot be null");
                }

                if (request.getProductItemId() == null || request.getProductItemId() <= 0) {
                        throw new BadRequestException("Invalid product item ID");
                }

                if (request.getQuantity() == null || request.getQuantity() <= 0) {
                        throw new BadRequestException("Quantity must be greater than 0");
                }

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + email));

                Cart cart = cartRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cart not found for user: " + user.getId()));

                ProductItem productItem = productItemRepository.findById(request.getProductItemId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "ProductItem not found with id: " + request.getProductItemId()));

                CartItem item = CartItem.builder()
                                .cart(cart)
                                .productItem(productItem)
                                .quantity(request.getQuantity())
                                .build();

                cartItemRepository.save(item);

                CartResponse response = cartMapper.toResponse(cart);
                String cacheKey = "cart:user:" + user.getId();
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(10));
                return response;
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

                return cartMapper.toResponse(cart);
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
        }
}
