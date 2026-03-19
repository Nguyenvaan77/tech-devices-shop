package com.example.web.service.imple;

import com.example.web.dto.category.request.CreateCategoryRequest;
import com.example.web.dto.category.response.CategoryResponse;
import com.example.web.entity.Category;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.CategoryMapper;
import com.example.web.repository.CategoryRepository;
import com.example.web.service.inter.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (request == null) {
            throw new BadRequestException("Category request cannot be null");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        Category category = categoryMapper.toEntity(request);
        category = categoryRepository.save(category);

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid category ID");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return categoryMapper.toResponse(category);
    }
}