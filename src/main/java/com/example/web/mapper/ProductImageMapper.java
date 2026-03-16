package com.example.web.mapper;

import com.example.web.entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    default String toUrl(ProductImage entity) {
        return entity.getImageUrl();
    }
}