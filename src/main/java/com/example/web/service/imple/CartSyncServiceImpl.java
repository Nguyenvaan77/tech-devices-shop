package com.example.web.service.imple;

import com.example.web.dto.cart.bycache.CartByCacheResponse;
import com.example.web.dto.cart.bycache.CartItemByCacheResponse;
import com.example.web.entity.Cart;
import com.example.web.entity.CartItem;
import com.example.web.entity.Product;
import com.example.web.entity.User;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.repository.CartItemRepository;
import com.example.web.repository.CartRepository;
import com.example.web.repository.ProductRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.CartRedisService;
import com.example.web.service.inter.CartSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartSyncServiceImpl implements CartSyncService{

    private final CartRedisService cartRedisService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    @Lazy
    private CartSyncServiceImpl self;

    @Override
    public void syncDirtyCarts() {
        log.info("Bắt đầu đồng bộ Cart từ Redis xuống Database.");
        Set<Long> dirtyUserIds = cartRedisService.getAllDirtyCarts();
        log.info("Số lượng dirty cart cần đồng bộ: {}", dirtyUserIds.size());

        for (Long userId : dirtyUserIds) {
            try {
                log.info("Đang đồng bộ UserId: {}", userId);
                self.syncUserCart(userId);
                cartRedisService.removeDirtyCart(userId);
                log.info("Đồng bộ thành công cho UserId: {}", userId);
            } catch (Exception e) {
                log.error("Đồng bộ thất bại cho UserId: {}. Lỗi: {}", userId, e.getMessage(), e);
            }
        }
        log.info("Hoàn tất quá trình đồng bộ Cart.");
    }

    @Transactional
    @Override
    public void syncUserCart(Long userId) {
        // Bước 1: Đọc Cart từ Redis
        CartByCacheResponse cacheCart = cartRedisService.getCart(userId);

        // Bước 2: Nếu Cart Redis không tồn tại hoặc không có item
        if (cacheCart == null || cacheCart.getListCartItems() == null || cacheCart.getListCartItems().isEmpty()) {
            cartItemRepository.deleteByUserId(userId);
            cartRepository.deleteByUserId(userId);
            return;
        }

        // Bước 3: Nếu Cart tồn tại: Tìm Cart trong DB theo userId. Nếu chưa có thì tạo Cart mới.
        Cart dbCart = cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            Cart newCart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build();
            return cartRepository.save(newCart);
        });

        // Bước 4: Xóa toàn bộ CartItem cũ của Cart đó trong DB
        cartItemRepository.deleteByCartId(dbCart.getId());

        // Bước 5: Convert CartByCacheResponse sang List<CartItem>
        List<CartItem> cartItemsToSave = new ArrayList<>();
        for (CartItemByCacheResponse cachedItem : cacheCart.getListCartItems()) {
            Product product = productRepository.findById(cachedItem.getProduct_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cachedItem.getProduct_id()));

            CartItem newCartItem = CartItem.builder()
                    .cart(dbCart)
                    .product(product)
                    .quantity(cachedItem.getQuantity())
                    .build();
            cartItemsToSave.add(newCartItem);
        }

        // Bước 6: saveAll(...) toàn bộ CartItem
        // cartItemRepository.saveAll(cartItemsToSave);]

        dbCart.getItems().clear();
        dbCart.getItems().addAll(cartItemsToSave);

        cartRepository.save(dbCart);
    }
}
