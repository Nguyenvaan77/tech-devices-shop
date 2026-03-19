package com.example.web.service.imple;

import com.example.web.dto.product.request.CreateProductRequest;
import com.example.web.dto.product.request.UpdateProductRequest;
import com.example.web.dto.product.request.ProductFilterRequest;
import com.example.web.dto.product.response.ProductResponse;
import com.example.web.dto.common.PageResponse;
import com.example.web.entity.Product;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.ProductMapper;
import com.example.web.repository.ProductRepository;
import com.example.web.service.inter.ProductService;
import com.example.web.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        if (request == null) {
            throw new BadRequestException("Product request cannot be null");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Product name is required");
        }

        if (request.getPrice() == null || request.getPrice().signum() <= 0) {
            throw new BadRequestException("Product price must be greater than 0");
        }

        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid product ID");
        }

        if (request == null) {
            throw new BadRequestException("Product request cannot be null");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productMapper.updateEntity(request, product);

        if (product.getPrice() != null && product.getPrice().signum() <= 0) {
            throw new BadRequestException("Product price must be greater than 0");
        }

        product = productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid product ID");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(ProductFilterRequest filter) {
        if (filter == null) {
            throw new BadRequestException("Filter request cannot be null");
        }

        // Validate pagination parameters
        int page = filter.getPage() != null && filter.getPage() >= 0 ? filter.getPage() : 0;
        int pageSize = filter.getPageSize() != null && filter.getPageSize() > 0 ? filter.getPageSize() : 10;

        // Validate sort parameters
        String sortBy = filter.getSortBy() != null && !filter.getSortBy().isBlank() ? filter.getSortBy() : "id";
        String sortDirection = filter.getSortDirection() != null && !filter.getSortDirection().isBlank()
                ? filter.getSortDirection().toUpperCase()
                : "DESC";

        Sort.Direction direction;
        try {
            direction = Sort.Direction.valueOf(sortDirection);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid sort direction: " + sortDirection);
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        Page<Product> productPage = productRepository.findAll(
                ProductSpecification.filterProducts(filter),
                pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .isFirst(productPage.isFirst())
                .isLast(productPage.isLast())
                .build();
    }

    @Override
    public void deleteProduct(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid product ID");
        }

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }

}