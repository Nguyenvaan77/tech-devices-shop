package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.product.request.CreateProductRequest;
import com.example.web.dto.product.request.UpdateProductRequest;
import com.example.web.service.inter.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Object getProducts() {

        return ApiResponse.success(
                productService.getProducts()
        );
    }

    @GetMapping("/{id}")
    public Object getProduct(@PathVariable Long id) {

        return ApiResponse.success(
                productService.getProduct(id)
        );
    }

    @PostMapping
    public Object createProduct(@RequestBody CreateProductRequest request) {

        return ApiResponse.created(
                productService.createProduct(request)
        );
    }

    @PutMapping("/{id}")
    public Object updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request
    ) {

        return ApiResponse.success(
                productService.updateProduct(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public Object deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ApiResponse.noContent();
    }
}