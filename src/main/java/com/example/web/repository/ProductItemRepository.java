package com.example.web.repository;

import com.example.web.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    List<ProductItem> findByProductId(Long productId);

    Optional<ProductItem> findByProductCode(String productCode);
}
