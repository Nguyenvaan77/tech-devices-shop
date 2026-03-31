package com.example.web.service.inter;

import com.example.web.dto.attribute.request.CreateAttributeTypeRequest;
import com.example.web.dto.attribute.response.AttributeTypeResponse;

import java.util.List;

public interface AttributeTypeService {
    AttributeTypeResponse create(CreateAttributeTypeRequest request);

    List<AttributeTypeResponse> getAll();

    AttributeTypeResponse getById(Long id);
}
