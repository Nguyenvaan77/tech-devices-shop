package com.example.web.repository;

import com.example.web.entity.ProductItemHasAttributeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductItemHasAttributeTypeRepository extends JpaRepository<ProductItemHasAttributeType, Long> {
    List<ProductItemHasAttributeType> findByProductItemId(Long productItemId);
}
