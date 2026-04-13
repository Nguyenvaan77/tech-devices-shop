package com.example.web.mapper;

import com.example.web.dto.product.ProductDTO;
import com.example.web.dto.product.request.CreateProductRequest;
import com.example.web.dto.product.request.UpdateProductRequest;
import com.example.web.dto.product.response.ProductResponse;
import com.example.web.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "businessOwner.id", target = "businessOwnerId")
    @Mapping(source = "images", target = "images")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)

    Product toEntity(ProductDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "businessOwner", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ratingAvg", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    Product toEntity(CreateProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "businessOwner", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "sales", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ratingAvg", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    Product updateEntity(UpdateProductRequest request, @MappingTarget Product entity);

    // entity to dto
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "businessOwner.id", target = "businessOwnerId")
    @Mapping(source = "images", target = "images")
    ProductDTO toDTO(Product entity);
}