package com.example.web.service.inter;

import com.example.web.dto.productimage.request.CreateProductImageRequest;
import com.example.web.dto.productimage.response.ProductImageResponse;

public interface ProductImageService {
    ProductImageResponse create(Long productId, CreateProductImageRequest request);

    void delete(Long id);
}
