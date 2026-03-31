package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.sale.request.CreateSaleRequest;
import com.example.web.dto.sale.response.SaleResponse;
import com.example.web.service.inter.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getAll() {
        List<SaleResponse> responses = saleService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> create(
            @RequestBody CreateSaleRequest request) {
        SaleResponse response = saleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
