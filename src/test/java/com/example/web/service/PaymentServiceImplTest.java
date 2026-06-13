package com.example.web.service;

import com.example.web.config.VnpayConfig;
import com.example.web.dto.payment.PaymentIpnResponse;
import com.example.web.dto.payment.PaymentUrlResponse;
import com.example.web.entity.Order;
import com.example.web.entity.Payment;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.repository.OrderRepository;
import com.example.web.repository.PaymentRepository;
import com.example.web.repository.ProductRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.imple.PaymentServiceImpl;
import com.example.web.service.inter.CartRedisService;
import com.example.web.util.OrderStatus;
import com.example.web.util.PaymentStatus;
import com.example.web.util.VnpayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartRedisService cartRedisService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VnpayConfig vnpayConfig;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@test.com").build();
        order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING_PAYMENT.name())
                .totalPrice(new BigDecimal("100000"))
                .build();
    }

    @Test
    void createVnpayPayment_Success() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(vnpayConfig.getTmnCode()).thenReturn("TMNCODE");
        when(vnpayConfig.getSecretKey()).thenReturn("SECRET");
        when(vnpayConfig.getReturnUrl()).thenReturn("http://return");
        when(vnpayConfig.getPayUrl()).thenReturn("http://pay");

        // Act
        PaymentUrlResponse response = paymentService.createVnpayPayment(1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.getPaymentUrl().startsWith("http://pay?"));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void createVnpayPayment_ThrowsWhenAlreadyPaid() {
        order.setStatus(OrderStatus.PAID.name());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BadRequestException.class, () -> paymentService.createVnpayPayment(1L));
    }

    @Test
    void processIpn_InvalidSignature() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_SecureHash", "wrong_hash");
        when(vnpayConfig.getSecretKey()).thenReturn("SECRET");

        PaymentIpnResponse response = paymentService.processIpn(params);

        assertEquals("97", response.getRspCode());
    }

    @Test
    void processIpn_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "TXN123");
        params.put("vnp_Amount", "10000000"); // 100,000 * 100
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00");

        String validHash = VnpayUtils.hashAllFields(params, "SECRET");
        params.put("vnp_SecureHash", validHash);

        Payment payment = Payment.builder().id(1L).amount(new BigDecimal("100000")).order(order).build();
        
        when(vnpayConfig.getSecretKey()).thenReturn("SECRET");
        when(paymentRepository.findByPaymentCodeWithLock("TXN123")).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(order));

        PaymentIpnResponse response = paymentService.processIpn(params);

        assertEquals("00", response.getRspCode());
        assertEquals(PaymentStatus.SUCCESS.name(), payment.getStatus());
        assertEquals(OrderStatus.PAID.name(), order.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(orderRepository, times(1)).save(order);
    }
}
