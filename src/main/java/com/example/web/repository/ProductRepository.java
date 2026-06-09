package com.example.web.repository;

import com.example.web.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBusinessOwnerId(Long businessOwnerId);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p WHERE p.quantityInStock > 0 AND p.isDeleted = false")
    List<Product> findInStock();

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isDeleted = false")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.quantityInStock < :threshold AND p.isDeleted = false")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

}