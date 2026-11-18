package com.example.web.service.inter;

import java.util.List;

import org.springframework.security.access.method.P;

import com.example.web.dto.productvideo.ProductVideoDTO;
import com.example.web.entity.ProductVideo;

public interface ProductVideoService {
    ProductVideo save(ProductVideoDTO dto);

    void deleteById(Long id);

    ProductVideo findById(Long id);

    List<ProductVideo> findByProductId(Long productId);
}
