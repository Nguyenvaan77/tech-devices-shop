package com.example.web.service.inter;

import com.example.web.dto.product.ProductDTO;
import com.example.web.dto.productimage.ProductImageDto;
import com.example.web.dto.productimage.request.CreateProductImageRequest;
import com.example.web.dto.productimage.response.ProductImageResponse;
import com.example.web.entity.Product;
import com.example.web.entity.ProductImage;

public interface ProductImageService {
    ProductImageResponse create(Long productId, CreateProductImageRequest request);

    ProductImage create(ProductImageDto dto);

    void delete(Long id);

    ProductImageDto findById(Long id);

    void deleteById(Long id);
}
