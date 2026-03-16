package com.example.web.mapper;

import com.example.web.dto.product.ProductDTO;
import com.example.web.dto.product.request.CreateProductRequest;
import com.example.web.dto.product.request.UpdateProductRequest;
import com.example.web.dto.product.response.ProductResponse;
import com.example.web.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO toDTO(Product entity);

    @Mapping(source = "category.id", target = "categoryId")
    ProductResponse toResponse(Product entity);

    Product toEntity(ProductDTO dto);

    Product toEntity(CreateProductRequest request);

    void updateEntity(UpdateProductRequest request, Product entity);
}