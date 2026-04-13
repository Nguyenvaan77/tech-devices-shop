package com.example.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.web.dto.productvideo.ProductVideoDTO;
import com.example.web.entity.ProductVideo;

@Mapper(componentModel = "spring")
public interface ProductVideoMapper {
    // TODO: Implement mapping methods for ProductVideo entity and DTOs
    @Mapping(target = "productId", source = "product.id")
    ProductVideoDTO toDTO(ProductVideo productVideo);

    @Mapping(target = "product", ignore = true) // Ignore ID when mapping from DTO to entity
    ProductVideo toEntity(ProductVideoDTO productVideoDTO);
}
