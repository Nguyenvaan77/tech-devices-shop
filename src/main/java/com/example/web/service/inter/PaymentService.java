package com.example.web.service.inter;

import com.example.web.dto.payment.PaymentIpnResponse;
import com.example.web.dto.payment.PaymentResponse;
import com.example.web.dto.payment.PaymentReturnResponse;
import com.example.web.dto.payment.PaymentUrlResponse;

import java.util.Map;

public interface PaymentService {
    PaymentUrlResponse createVnpayPayment(Long orderId);
    PaymentIpnResponse processIpn(Map<String, String> params);
    PaymentResponse getPaymentByOrder(Long orderId);
    PaymentReturnResponse getPaymentInformationFromReturnUrl(Map<String,String> params);
}
