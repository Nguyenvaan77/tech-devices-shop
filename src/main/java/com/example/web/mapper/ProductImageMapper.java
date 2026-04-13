package com.example.web.mapper;

import com.example.web.dto.productimage.ProductImageDto;
import com.example.web.dto.productimage.request.CreateProductImageRequest;
import com.example.web.dto.productimage.response.ProductImageResponse;
import com.example.web.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageResponse toResponse(ProductImage entity);
    
    @Mapping(target = "product", ignore = true)
    ProductImage toEntity(ProductImageDto dto);

    @Mapping(target = "product", ignore = true)
    ProductImage toEntity(CreateProductImageRequest request);

    // entity to dto 
    @Mapping(target = "productId", source = "product.id")
    ProductImageDto toDto(ProductImage entity);

    // // dto to entity
    // @Mapping(target = "product", ignore = true)
    // ProductImage toEntity(ProductImageDto dto);
}