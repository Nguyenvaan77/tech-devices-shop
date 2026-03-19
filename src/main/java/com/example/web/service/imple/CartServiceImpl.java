package com.example.web.service.imple;

import com.example.web.dto.cart.request.AddToCartRequest;
import com.example.web.dto.cart.response.CartResponse;
import com.example.web.entity.Cart;
import com.example.web.entity.CartItem;
import com.example.web.entity.Product;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.CartMapper;
import com.example.web.repository.CartItemRepository;
import com.example.web.repository.CartRepository;
import com.example.web.repository.ProductRepository;
import com.example.web.service.inter.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final ProductRepository productRepository;
        private final CartMapper cartMapper;

        @Override
        @Transactional(readOnly = true)
        public CartResponse getCart(Long userId) {
                if (userId == null || userId <= 0) {
                        throw new BadRequestException("Invalid user ID");
                }

                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

                return cartMapper.toResponse(cart);
        }

        @Override
        public CartResponse addToCart(Long userId, AddToCartRequest request) {
                if (userId == null || userId <= 0) {
                        throw new BadRequestException("Invalid user ID");
                }

                if (request == null) {
                        throw new BadRequestException("Add to cart request cannot be null");
                }

                if (request.getProductId() == null || request.getProductId() <= 0) {
                        throw new BadRequestException("Invalid product ID");
                }

                if (request.getQuantity() == null || request.getQuantity() <= 0) {
                        throw new BadRequestException("Quantity must be greater than 0");
                }

                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found with id: " + request.getProductId()));

                CartItem item = new CartItem();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(request.getQuantity());

                cartItemRepository.save(item);

                return cartMapper.toResponse(cart);
        }

        @Override
        public CartResponse removeFromCart(Long userId, Long productId) {
                if (userId == null || userId <= 0) {
                        throw new BadRequestException("Invalid user ID");
                }

                if (productId == null || productId <= 0) {
                        throw new BadRequestException("Invalid product ID");
                }

                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

                CartItem item = cartItemRepository
                                .findByCartIdAndProductId(cart.getId(), productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

                cartItemRepository.delete(item);

                return cartMapper.toResponse(cart);
        }
}