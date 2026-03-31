package com.example.web.repository;

import com.example.web.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProductItemAttributeId(Long productItemAttributeId);
}
