package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/{orderId}/payments")
public class PaymentController {

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> pay(
            @PathVariable Long orderId,
            @RequestBody Object request) {
        // TODO: Implement payment logic
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }
}

@RestController
@RequestMapping("/api/payments/webhook")
class PaymentWebhookController {

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> webhook(@RequestBody Object request) {
        // TODO: Implement webhook logic
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
