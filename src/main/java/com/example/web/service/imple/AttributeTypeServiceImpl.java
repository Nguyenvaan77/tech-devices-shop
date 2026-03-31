package com.example.web.service.imple;

import com.example.web.dto.attribute.request.CreateAttributeTypeRequest;
import com.example.web.dto.attribute.response.AttributeTypeResponse;
import com.example.web.entity.AttributeType;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.AttributeTypeMapper;
import com.example.web.repository.AttributeTypeRepository;
import com.example.web.service.inter.AttributeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeTypeServiceImpl implements AttributeTypeService {

    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeTypeMapper attributeTypeMapper;

    @Override
    public AttributeTypeResponse create(CreateAttributeTypeRequest request) {
        AttributeType attributeType = attributeTypeMapper.toEntity(request);
        AttributeType saved = attributeTypeRepository.save(attributeType);
        return attributeTypeMapper.toResponse(saved);
    }

    @Override
    public List<AttributeTypeResponse> getAll() {
        return attributeTypeRepository.findAll()
                .stream()
                .map(attributeTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AttributeTypeResponse getById(Long id) {
        AttributeType attributeType = attributeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeType not found with id: " + id));
        return attributeTypeMapper.toResponse(attributeType);
    }
}
