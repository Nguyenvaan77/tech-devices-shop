package com.example.web.mapper;

import com.example.web.dto.productitem.request.CreateProductItemRequest;
import com.example.web.dto.productitem.response.ProductItemResponse;
import com.example.web.entity.ProductItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductItemMapper {
    ProductItemResponse toResponse(ProductItem entity);

    ProductItem toEntity(CreateProductItemRequest dto);
}
