package com.example.web.mapper;

import com.example.web.dto.category.request.CreateCategoryRequest;
import com.example.web.dto.category.response.CategoryResponse;
import com.example.web.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "parent.id", target = "parentId")
    CategoryResponse toResponse(Category entity);

    Category toEntity(CreateCategoryRequest request);
}