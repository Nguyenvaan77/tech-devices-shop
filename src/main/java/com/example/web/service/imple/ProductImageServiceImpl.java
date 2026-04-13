package com.example.web.service.imple;

import com.example.web.dto.product.ProductDTO;
import com.example.web.dto.productimage.ProductImageDto;
import com.example.web.dto.productimage.request.CreateProductImageRequest;
import com.example.web.dto.productimage.response.ProductImageResponse;
import com.example.web.entity.Product;
import com.example.web.entity.ProductImage;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.ProductImageMapper;
import com.example.web.repository.ProductImageRepository;
import com.example.web.repository.ProductRepository;
import com.example.web.service.inter.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    public ProductImageDto findById(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage not found with id: " + id));
        return productImageMapper.toDto(productImage);
    }

    @Override
    public ProductImageResponse create(Long productId, CreateProductImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductImage productImage = productImageMapper.toEntity(request);
        productImage.setProduct(product);

        ProductImage saved = productImageRepository.save(productImage);
        return productImageMapper.toResponse(saved);
    }

    

    @Override
    public ProductImage create(ProductImageDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        ProductImage productImage = productImageMapper.toEntity(dto);
        productImage.setProduct(product);

        return productImageRepository.save(productImage);
    }



    @Override
    public void delete(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage not found with id: " + id));
        productImageRepository.delete(productImage);
    }

    @Override
    public void deleteById(Long id) {
        if (!productImageRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProductImage not found with id: " + id);
        }
        productImageRepository.deleteById(id);
    }
}
