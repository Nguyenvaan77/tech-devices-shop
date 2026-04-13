package com.example.web.service.imple;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.web.dto.productvideo.ProductVideoDTO;
import com.example.web.entity.Product;
import com.example.web.entity.ProductVideo;
import com.example.web.mapper.ProductVideoMapper;
import com.example.web.repository.ProductRepository;
import com.example.web.repository.ProductVideoRepository;
import com.example.web.service.inter.ProductVideoService;

@Service
public class ProductVideoServiceImpl implements ProductVideoService{

    private final ProductVideoRepository productVideoRepository;
    private final ProductVideoMapper productVideoMapper;
    private final ProductRepository productRepository;

    public ProductVideoServiceImpl(ProductVideoRepository productVideoRepository, ProductVideoMapper productVideoMapper, ProductRepository productRepository) {
        this.productVideoRepository = productVideoRepository;
        this.productVideoMapper = productVideoMapper;
        this.productRepository = productRepository;
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        if(!productVideoRepository.existsById(id)) {
            throw new RuntimeException("Product video not found with id: " + id);
        }
        productVideoRepository.deleteById(id);
    }

    @Override
    public ProductVideo findById(Long id) {
        // TODO Auto-generated method stub
            return productVideoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product video not found with id: " + id));
            
    }

    @Override
    public List<ProductVideo> findByProductId(Long productId) {
        // TODO Auto-generated method stub
        return productVideoRepository.findByProductId(productId);
    }

    @Override
    public ProductVideo save(ProductVideoDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProductId()));

        ProductVideo entity = productVideoMapper.toEntity(dto);
        entity.setProduct(product);

        return productVideoRepository.save(entity);
    }
    
}
