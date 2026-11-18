package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.productimage.request.CreateProductImageRequest;
import com.example.web.dto.productimage.response.ProductImageResponse;
import com.example.web.service.inter.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductImageResponse>> upload(
            @PathVariable Long productId,
            @RequestBody CreateProductImageRequest request) {
        ProductImageResponse response = productImageService.create(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
class ProductImageDetailController {

    private final ProductImageService productImageService;

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productImageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
