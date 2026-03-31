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

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        return null;
    }

    @Override
    public CategoryResponse updateCategory(Long id, CreateCategoryRequest request) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid category ID");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName());
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            category.setSlug(request.getSlug());
        }

        if (request.getParentId() != null) {
            if (request.getParentId() > 0) {
                Category parent = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Parent category not found with id: " + request.getParentId()));
                category.setParent(parent);
            } else {
                category.setParent(null);
            }
        }

        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid category ID");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setIsDeleted(true);
        categoryRepository.save(category);
    }
}
