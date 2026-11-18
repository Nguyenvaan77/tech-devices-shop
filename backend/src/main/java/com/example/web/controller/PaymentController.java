package com.example.web.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web.config.VnpayConfig;
import com.example.web.dto.ApiResponse;
import com.example.web.dto.payment.PaymentIpnResponse;
import com.example.web.dto.payment.PaymentReturnResponse;
import com.example.web.dto.payment.PaymentUrlResponse;
import com.example.web.service.inter.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/vnpay/{orderId}")
    public ResponseEntity<ApiResponse<PaymentUrlResponse>> createPayment(@PathVariable Long orderId) {
        PaymentUrlResponse response = paymentService.createVnpayPayment(orderId);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment URL generated successfully", 200));
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<ApiResponse<PaymentReturnResponse>> vnpayReturn(@RequestParam Map<String, String> allParams) {
        PaymentReturnResponse response = paymentService.getPaymentInformationFromReturnUrl(allParams);
        return ResponseEntity.ok(ApiResponse.success(response, response.getMessage(), 200));
    }

    @GetMapping("/vnpay-ipn")
    public PaymentIpnResponse vnpayIpn(@RequestParam Map<String, String> allParams) {
        // Trả về trực tiếp object thay vì ResponseEntity<ApiResponse>
        // Vì VNPAY server sẽ parse JSON response này
        return paymentService.processIpn(allParams);
    }
}
