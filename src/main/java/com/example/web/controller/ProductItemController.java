package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.productitem.request.CreateProductItemRequest;
import com.example.web.dto.productitem.response.ProductItemResponse;
import com.example.web.service.inter.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/items")
@RequiredArgsConstructor
public class ProductItemController {

    private final ProductItemService productItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductItemResponse>> create(
            @PathVariable Long productId,
            @RequestBody CreateProductItemRequest request) {
        ProductItemResponse response = productItemService.create(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductItemResponse>>> getByProductId(
            @PathVariable Long productId) {
        List<ProductItemResponse> responses = productItemService.getByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}

@RestController
@RequestMapping("/api/product-items")
@RequiredArgsConstructor
class ProductItemDetailController {

    private final ProductItemService productItemService;

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductItemResponse>> update(
            @PathVariable Long id,
            @RequestBody CreateProductItemRequest request) {
        ProductItemResponse response = productItemService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
