package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.common.PageResponse;
import com.example.web.dto.product.request.CreateProductRequest;
import com.example.web.dto.product.request.UpdateProductRequest;
import com.example.web.dto.product.request.ProductFilterRequest;
import com.example.web.dto.product.response.ProductResponse;
import com.example.web.service.inter.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

        private final ProductService productService;

        @GetMapping
        @PreAuthorize("hasAuthority('PRODUCT_READ')")
        public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts() {
                List<ProductResponse> products = productService.getProducts();
                return ResponseEntity.ok(ApiResponse.success(products));
        }

        @GetMapping("/search")
        @PreAuthorize("hasAuthority('PRODUCT_READ')")
        public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
                        @ModelAttribute ProductFilterRequest filter) {
                PageResponse<ProductResponse> result = productService.searchProducts(filter);
                return ResponseEntity.ok(ApiResponse.success(result));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('PRODUCT_READ')")
        public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
                ProductResponse product = productService.getProduct(id);
                return ResponseEntity.ok(ApiResponse.success(product));
        }

        @PostMapping
        @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
        public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
                        @RequestBody CreateProductRequest request) {
                ProductResponse product = productService.createProduct(request);

                return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(product));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
        public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
                        @PathVariable Long id,
                        @RequestBody UpdateProductRequest request) {
                ProductResponse product = productService.updateProduct(id, request);
                return ResponseEntity.ok(ApiResponse.success(product));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
        public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
                productService.deleteProduct(id);
                return ResponseEntity.noContent().build();
        }
}