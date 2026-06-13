package com.example.web.service.imple;

import com.example.web.config.VnpayConfig;
import com.example.web.dto.payment.PaymentIpnResponse;
import com.example.web.dto.payment.PaymentResponse;
import com.example.web.dto.payment.PaymentReturnResponse;
import com.example.web.dto.payment.PaymentUrlResponse;
import com.example.web.entity.Order;
import com.example.web.entity.OrderItem;
import com.example.web.entity.Payment;
import com.example.web.entity.Product;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.InvalidSignatureException;
import com.example.web.exception.PaymentException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.repository.OrderRepository;
import com.example.web.repository.PaymentRepository;
import com.example.web.repository.ProductRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.CartRedisService;
import com.example.web.service.inter.PaymentService;
import com.example.web.util.OrderStatus;
import com.example.web.util.PaymentStatus;
import com.example.web.util.VnpayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRedisService cartRedisService;
    private final UserRepository userRepository;
    private final VnpayConfig vnpayConfig;

    @Override
    @Transactional
    public PaymentUrlResponse createVnpayPayment(Long orderId) {
        log.info("Create Payment for orderId: {}", orderId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to pay for this order");
        }

        if (OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BadRequestException("Order is already paid");
        }

        if (!OrderStatus.PENDING_PAYMENT.name().equals(order.getStatus())) {
            throw new BadRequestException("Order is not in PENDING_PAYMENT status");
        }

        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
        String paymentCode;

        if (payment != null) {
            paymentCode = payment.getPaymentCode();
            payment.setAmount(order.getTotalPrice());
            payment.setUpdatedAt(LocalDateTime.now());
        } else {
            paymentCode = VnpayUtils.generateTxnRef();
            payment = Payment.builder()
                    .order(order)
                    .paymentCode(paymentCode)
                    .amount(order.getTotalPrice())
                    .status(PaymentStatus.PENDING.name())
                    .paymentMethod("VNPAY")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
        paymentRepository.save(payment);

        long amount = order.getTotalPrice().longValue() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", paymentCode);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        String hashData = VnpayUtils.hashAllFields(vnp_Params, vnpayConfig.getSecretKey().trim());
        String queryUrl = buildQueryUrl(vnp_Params) + "&vnp_SecureHash=" + hashData;
        String paymentUrl = vnpayConfig.getPayUrl() + "?" + queryUrl;

        return PaymentUrlResponse.builder().paymentUrl(paymentUrl).build();
    }

    private String buildQueryUrl(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                if (!first) {
                    sb.append("&");
                }
                first = false;
                sb.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                sb.append("=");
                sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            }
        }
        return sb.toString();
    }


    @Override
    public PaymentReturnResponse getPaymentInformationFromReturnUrl(Map<String, String> allParams) {
        String vnp_SecureHash = allParams.get("vnp_SecureHash");
        Map<String, String> params = new HashMap<>(allParams);
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String hashData = VnpayUtils.hashAllFields(params, vnpayConfig.getSecretKey());

        PaymentReturnResponse response = new PaymentReturnResponse();
        response.setPaymentCode(params.get("vnp_TxnRef"));
        if (params.get("vnp_Amount") != null) {
            response.setAmount(new BigDecimal(params.get("vnp_Amount")).divide(new BigDecimal(100)));
        }
        response.setGatewayTransactionId(params.get("vnp_TransactionNo"));
        response.setBankCode(params.get("vnp_BankCode"));

        if (!hashData.equals(vnp_SecureHash)) {
            throw new InvalidSignatureException("This secureHash is not match with VNPay server");
        }

        if ("00".equals(params.get("vnp_ResponseCode")) && "00".equals(params.get("vnp_TransactionStatus"))) {
            response.setStatus("SUCCESS");
            response.setMessage("Thanh toán thành công");
        } else {
            response.setStatus("FAILED");
            response.setMessage("Thanh toán thất bại");
        }

        
        return response;
    }

    @Override
    @Transactional
    public PaymentIpnResponse processIpn(Map<String, String> params) {
        log.info("Process VNPAY IPN: {}", params);

        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String hashData = VnpayUtils.hashAllFields(params, vnpayConfig.getSecretKey().trim());
        if (!hashData.equals(vnp_SecureHash)) {
            log.warn("Invalid Signature");
            return new PaymentIpnResponse("97", "Invalid Signature");
        }

        String paymentCode = params.get("vnp_TxnRef");

        Payment payment = paymentRepository.findByPaymentCodeWithLock(paymentCode)
                .orElse(null);

        if (payment == null) {
            log.warn("Order not found with paymentCode: {}", paymentCode);
            return new PaymentIpnResponse("01", "Order not found");
        }

        Order order = orderRepository.findByIdWithLock(payment.getOrder().getId())
                .orElse(null);

        if (order == null) {
            log.warn("Order not found for id: {}", payment.getOrder().getId());
            return new PaymentIpnResponse("01", "Order not found");
        }

        long vnpAmount = Long.parseLong(params.get("vnp_Amount")) / 100;
        if (payment.getAmount().longValue() != vnpAmount) {
            log.warn("Amount Mismatch");
            return new PaymentIpnResponse("04", "Invalid amount");
        }

        if (PaymentStatus.SUCCESS.name().equals(payment.getStatus())) {
            log.info("Duplicate IPN - already SUCCESS");
            return new PaymentIpnResponse("02", "Order already confirmed");
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");

        payment.setGatewayTransactionId(params.get("vnp_TransactionNo"));
        payment.setResponseCode(responseCode);
        payment.setBankCode(params.get("vnp_BankCode"));
        payment.setUpdatedAt(LocalDateTime.now());

        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            payment.setStatus(PaymentStatus.SUCCESS.name());
            order.setStatus(OrderStatus.PAID.name());

            // Deduct Inventory
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Product product = item.getProduct();
                    int newStock = product.getQuantityInStock() - item.getQuantity();
                    if(newStock < 0) newStock = 0;
                    product.setQuantityInStock(newStock);
                    productRepository.save(product);
                }
            }
            log.info("Payment Success, Inventory Updated");

            // Delete Cart
            try {
                cartRedisService.clearCart(order.getUser().getId());
                log.info("Cart Deleted");
            } catch (Exception e) {
                log.error("Failed to delete cart", e);
            }

        } else {
            payment.setStatus(PaymentStatus.FAILED.name());
            log.info("Payment Failed");
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        return new PaymentIpnResponse("00", "Confirm Success");
    }

    @Override
    public PaymentResponse getPaymentByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentCode(payment.getPaymentCode())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .responseCode(payment.getResponseCode())
                .bankCode(payment.getBankCode())
                .transactionTime(payment.getTransactionTime())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .orderId(payment.getOrder().getId())
                .build();
    }
}
