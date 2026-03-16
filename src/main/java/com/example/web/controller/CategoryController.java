package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.category.request.CreateCategoryRequest;
import com.example.web.service.inter.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Object getCategories() {

        return ApiResponse.success(
                categoryService.getAllCategories()
        );
    }

    @GetMapping("/{id}")
    public Object getCategory(@PathVariable Long id) {

        return ApiResponse.success(
                categoryService.getCategory(id)
        );
    }

    @PostMapping
    public Object createCategory(@RequestBody CreateCategoryRequest request) {

        return ApiResponse.created(
                categoryService.createCategory(request)
        );
    }
}