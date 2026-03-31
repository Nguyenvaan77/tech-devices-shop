package com.example.web.mapper;

import com.example.web.dto.attribute.request.CreateAttributeTypeRequest;
import com.example.web.dto.attribute.response.AttributeTypeResponse;
import com.example.web.entity.AttributeType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttributeTypeMapper {
    AttributeTypeResponse toResponse(AttributeType entity);

    AttributeType toEntity(CreateAttributeTypeRequest dto);
}
