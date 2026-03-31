package com.example.web.service.inter;

import com.example.web.dto.productitem.request.CreateProductItemRequest;
import com.example.web.dto.productitem.response.ProductItemResponse;

import java.util.List;

public interface ProductItemService {
    ProductItemResponse create(Long productId, CreateProductItemRequest request);

    ProductItemResponse getById(Long id);

    List<ProductItemResponse> getByProductId(Long productId);

    ProductItemResponse update(Long id, CreateProductItemRequest request);

    void delete(Long id);
}
