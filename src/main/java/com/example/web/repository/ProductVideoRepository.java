package com.example.web.repository;

import com.example.web.entity.ProductVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVideoRepository extends JpaRepository<ProductVideo, Long> {
    List<ProductVideo> findByProductId(Long productId);
}
