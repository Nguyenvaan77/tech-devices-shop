package com.example.web.service.imple;

import com.example.web.dto.cart.bycache.CartByCacheResponse;
import com.example.web.dto.cart.bycache.CartItemByCacheResponse;
import com.example.web.dto.order.request.CreateOrderRequest;
import com.example.web.dto.order.response.OrderResponse;
import com.example.web.dto.order.response.OrderItemResponse;
import com.example.web.entity.*;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.InsufficientStockException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.OrderMapper;
import com.example.web.repository.*;
import com.example.web.service.inter.CartRedisService;
import com.example.web.service.inter.OrderService;
import com.example.web.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.web.exception.MissingIdempotencyKeyException;
import com.example.web.exception.OrderProcessingException;
import com.example.web.dto.order.IdempotencyRecord;
import com.example.web.util.IdempotencyStatus;
import com.example.web.service.inter.IdempotencyService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {
 
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderMapper orderMapper;
    private final CartRedisService cartRedisService;
    private final IdempotencyService idempotencyService;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey) {

        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            throw new MissingIdempotencyKeyException("Idempotency-Key header is required");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication != null ? authentication.getName() : "unknown";

        IdempotencyRecord existingRecord = idempotencyService.get(idempotencyKey);
        if (existingRecord != null) {
            if (IdempotencyStatus.PROCESSING.equals(existingRecord.getStatus())) {
                log.warn("DUPLICATE_PROCESSING - userId: {}, idempotencyKey: {}", currentEmail, idempotencyKey);
                throw new OrderProcessingException("Order creation is already in progress.");
            } else if (IdempotencyStatus.SUCCESS.equals(existingRecord.getStatus())) {
                log.info("RETRY_SUCCESS - userId: {}, idempotencyKey: {}, orderId: {}", currentEmail, idempotencyKey, existingRecord.getOrderId());
                return getOrder(existingRecord.getOrderId());
            } else if (IdempotencyStatus.FAILED.equals(existingRecord.getStatus())) {
                idempotencyService.retryProcessing(idempotencyKey);
                log.info("START_PROCESSING (Retry) - userId: {}, idempotencyKey: {}", currentEmail, idempotencyKey);
            }
        } else {
            boolean isProcessing = idempotencyService.startProcessing(idempotencyKey);
            if (!isProcessing) {
                log.warn("DUPLICATE_PROCESSING - userId: {}, idempotencyKey: {}", currentEmail, idempotencyKey);
                throw new OrderProcessingException("Order creation is already in progress.");
            }
            log.info("START_PROCESSING - userId: {}, idempotencyKey: {}", currentEmail, idempotencyKey);
        }

        try {

        if (request == null) {
            throw new BadRequestException("Order request cannot be null");
        }

        if (request.getPaymentMethod() == null
                || request.getPaymentMethod().isBlank()) {
            throw new BadRequestException("Payment method is required");
        }

        if (request.getAddressId() == null
                || request.getAddressId() <= 0) {
            throw new BadRequestException("Address is required");
        }

        String email = currentEmail;

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );

        Address address = addressRepository.findById(
                        request.getAddressId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: "
                                        + request.getAddressId()
                        )
                );

        CartByCacheResponse cart =
                cartRedisService.getCart(user.getId());

        if (cart == null
                || cart.getListCartItems() == null
                || cart.getListCartItems().isEmpty()) {

            throw new BadRequestException(
                    "Cannot create order from an empty cart"
            );
        }

        List<CartItemByCacheResponse> cartItems =
                cart.getListCartItems();

        Map<Long, Product> productMap = new HashMap<>();

        /*
        * Validate product + stock
        */
        for (CartItemByCacheResponse item : cartItems) {

            Product product = productRepository.findById(
                            item.getProduct_id()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found: "
                                            + item.getProduct_id()
                            )
                    );

            if (product.getQuantityInStock()
                    < item.getQuantity()) {

                throw new InsufficientStockException(
                        "Not enough stock for product: "
                                + product.getName()
                );
            }

            productMap.put(product.getId(), product);
        }

        /*
        * Create Order
        */
        Order order = Order.builder()
                .user(user)
                .address(address)
                .status(OrderStatus.PENDING_PAYMENT.name())
                .paymentMethod(request.getPaymentMethod())
                .createdAt(LocalDateTime.now())
                .totalPrice(BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);

        BigDecimal totalPrice = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();



        /*
        * Create OrderItem
        */
        for (CartItemByCacheResponse item : cartItems) {

            Product product =
                    productMap.get(item.getProduct_id());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);

            totalPrice = totalPrice.add(
                    product.getPrice().multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()
                            )
                    )
            );
        }

        orderItemRepository.saveAll(orderItems);

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        order = orderRepository.save(order);

        final Long orderId = order.getId();

        /*
        * Delete Cart DB
        * (Cart phải có cascade remove xuống CartItem)
        */
        cartRepository.findByUserId(user.getId())
                .ifPresent(cartRepository::delete);

        /*
        * Delete Redis AFTER COMMIT
        */
        Long userId = user.getId();

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                cartRedisService.clearCart(userId);
                                idempotencyService.markSuccess(idempotencyKey, orderId);
                                log.info("SUCCESS - userId: {}, idempotencyKey: {}, orderId: {}", currentEmail, idempotencyKey, orderId);
                            }
                        }
                );

            

            return orderMapper.toResponse(order);
        } catch (Exception e) {
            idempotencyService.markFailed(idempotencyKey);
            log.error("FAILED - userId: {}, idempotencyKey: {}, error: {}", currentEmail, idempotencyKey, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BadRequestException("Invalid order ID");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (OrderStatus.CANCELLED.name().equals(order.getStatus())) {
            throw new BadRequestException("Order is already cancelled");
        }

        // Hoàn trả tồn kho nếu đã thanh toán
        if (OrderStatus.PAID.name().equals(order.getStatus()) && order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantityInStock(product.getQuantityInStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELLED.name());
        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItems(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BadRequestException("Invalid order ID");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(orderMapper::toItemResponse)
                .collect(Collectors.toList());
    }
}
