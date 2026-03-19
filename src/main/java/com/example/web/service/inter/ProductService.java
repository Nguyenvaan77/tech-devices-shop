package com.example.web.service.inter;

import com.example.web.dto.product.request.CreateProductRequest;
import com.example.web.dto.product.request.UpdateProductRequest;
import com.example.web.dto.product.request.ProductFilterRequest;
import com.example.web.dto.product.response.ProductResponse;
import com.example.web.dto.common.PageResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    ProductResponse getProduct(Long id);

    List<ProductResponse> getProducts();

    PageResponse<ProductResponse> searchProducts(ProductFilterRequest filter);

    void deleteProduct(Long id);
}