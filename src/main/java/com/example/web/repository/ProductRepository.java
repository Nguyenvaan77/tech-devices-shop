package com.example.web.repository;

import com.example.web.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrand(String brand);

    List<Product> findByNameContainingIgnoreCase(String keyword);

}