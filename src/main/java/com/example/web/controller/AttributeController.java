package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.attribute.request.CreateAttributeTypeRequest;
import com.example.web.dto.attribute.response.AttributeTypeResponse;
import com.example.web.service.inter.AttributeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeTypeService attributeTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttributeTypeResponse>>> getAll() {
        List<AttributeTypeResponse> responses = attributeTypeService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AttributeTypeResponse>> create(
            @RequestBody CreateAttributeTypeRequest request) {
        AttributeTypeResponse response = attributeTypeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
