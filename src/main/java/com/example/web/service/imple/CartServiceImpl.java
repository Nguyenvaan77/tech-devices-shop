package com.example.web.service.imple;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web.dto.cart.bycache.CartByCacheResponse;
import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.request.RemoveFromCartRequest;
import com.example.web.dto.cart.response.CartItemResponse;
import com.example.web.dto.cart.response.CartResponse;
import com.example.web.entity.Cart;
import com.example.web.entity.CartItem;
import com.example.web.entity.Product;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.InsufficientStockException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.CartMapper;
import com.example.web.repository.CartItemRepository;
import com.example.web.repository.CartRepository;
import com.example.web.repository.ProductRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.AuthService;
import com.example.web.service.inter.CartRedisService;
import com.example.web.service.inter.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final CartMapper cartMapper;

        private final CartRedisService cartRedisService;

        @Override
        // public CartResponse getCurrentCart(Long userId) {
        public CartByCacheResponse getCurrentCart(Long userId) {
                
                // Cart cart = cartRepository.findByUserId(userId)
                //         .orElseGet(() -> {
                //                 User user = userRepository.findById(userId)
                //                                 .orElseThrow(() -> new ResourceNotFoundException(
                //                                                 "User not found with id: " + userId));
                //                 Cart newCart = Cart.builder()
                //                                 .user(user)
                //                                 .createdAt(LocalDateTime.now())
                //                                 .items(new ArrayList<>())
                //                                 .build();
                //                 return cartRepository.save(newCart);
                //         });

                // CartByCacheResponse response = cartMapper.toCartByCacheResponse(cart);

                // return response;
                return cartRedisService.getCart(userId);
        }

        @Override
        public CartByCacheResponse addToCart(AddToCartRequest request) {

                // 1. Validate input
                if (request == null) {
                        throw new BadRequestException("Add to cart request cannot be null");
                }

                if (request.getProductId() == null || request.getProductId() <= 0) {
                        throw new BadRequestException("Invalid product ID");
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

                // // 3. Lấy hoặc tạo cart
                // Cart cart = cartRepository.findByUserId(user.getId())
                //         .orElseGet(() -> {
                //                 Cart newCart = Cart.builder()
                //                                 .user(user)
                //                                 .createdAt(LocalDateTime.now())
                //                                 .items(new java.util.ArrayList<>())
                //                                 .build();
                //                 return cartRepository.save(newCart);
                //         });

                // // 4. Lấy product
                // Product product = productRepository.findById(request.getProductId())
                //         .orElseThrow(() -> new ResourceNotFoundException(
                //                 "Product not found with id: " + request.getProductId()));

                // // 5. Kiểm tra tồn kho
                // if (product.getQuantityInStock() < request.getQuantity()) {
                //         throw new InsufficientStockException("Not enough stock for product: " + product.getName());
                // }

                // // 6. Kiểm tra đã tồn tại trong cart chưa
                // CartItem existingItem = cartItemRepository
                //         .findByCartIdAndProductId(cart.getId(), product.getId())
                //         .orElse(null);

                // int newQuantity;

                // if (existingItem != null) {
                //         // CASE 1: đã tồn tại → cộng dồn
                //         if (product.getQuantityInStock() < existingItem.getQuantity() + request.getQuantity()) {
                //                 throw new InsufficientStockException("Not enough stock for product: " + product.getName());
                //         }
                //         newQuantity = existingItem.getQuantity() + request.getQuantity();
                //         existingItem.setQuantity(newQuantity);
                //         cartItemRepository.save(existingItem);
                // } else {
                //         // CASE 2: chưa tồn tại → tạo mới
                //         newQuantity = request.getQuantity();

                //         CartItem newItem = CartItem.builder()
                //                 .cart(cart)
                //                 .product(product)
                //                 .quantity(newQuantity)
                //                 .build();

                //         cartItemRepository.save(newItem);
                // }

                CartByCacheResponse response = cartRedisService.addToCart(user.getId(), request.getProductId(), request.getQuantity());

                return response;
        }

        @Override
        public CartByCacheResponse removeFromCart(RemoveFromCartRequest request) {
                // if (cartItemId == null || cartItemId <= 0) {
                //         throw new BadRequestException("Invalid cart item ID");
                // }

                // CartItem item = cartItemRepository.findById(cartItemId)
                //                 .orElseThrow(() -> new ResourceNotFoundException(
                //                                 "CartItem not found with id: " + cartItemId));

                // Cart cart = item.getCart();
                // cartItemRepository.delete(item);

                if(request == null || request.getProductId() == null || request.getQuantity() == null) throw new BadRequestException("Invalid cart or product id or quantity");

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with email: " + email));

                CartByCacheResponse response = cartRedisService.removeFromCart(user.getId(), request.getProductId(), request.getQuantity());

                return response;
        }

        @Override
        public void clearCart() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with email: " + email));

                // Cart cart = cartRepository.findByUserId(user.getId())
                //                 .orElseThrow(() -> new ResourceNotFoundException(
                //                                 "Cart not found for user: " + user.getId()));

                // cartItemRepository.deleteAll(cart.getItems());

                cartRedisService.clearCart(user.getId());
        }
}
