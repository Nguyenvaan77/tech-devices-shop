package com.example.web.service.imple;

import com.example.web.dto.productitem.request.CreateProductItemRequest;
import com.example.web.dto.productitem.response.ProductItemResponse;
import com.example.web.entity.Product;
import com.example.web.entity.ProductItem;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.ProductItemMapper;
import com.example.web.repository.ProductItemRepository;
import com.example.web.repository.ProductRepository;
import com.example.web.service.inter.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductItemServiceImpl implements ProductItemService {

    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;
    private final ProductItemMapper productItemMapper;

    @Override
    public ProductItemResponse create(Long productId, CreateProductItemRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductItem productItem = productItemMapper.toEntity(request);
        productItem.setProduct(product);

        ProductItem saved = productItemRepository.save(productItem);
        return productItemMapper.toResponse(saved);
    }

    @Override
    public ProductItemResponse getById(Long id) {
        ProductItem productItem = productItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductItem not found with id: " + id));
        return productItemMapper.toResponse(productItem);
    }

    @Override
    public List<ProductItemResponse> getByProductId(Long productId) {
        return productItemRepository.findByProductId(productId)
                .stream()
                .map(productItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductItemResponse update(Long id, CreateProductItemRequest request) {
        ProductItem productItem = productItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductItem not found with id: " + id));

        if (request.getProductCode() != null) {
            productItem.setProductCode(request.getProductCode());
        }
        if (request.getOriginalPrice() != null) {
            productItem.setOriginalPrice(request.getOriginalPrice());
        }

        ProductItem updated = productItemRepository.save(productItem);
        return productItemMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        ProductItem productItem = productItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductItem not found with id: " + id));
        productItemRepository.delete(productItem);
    }
}
