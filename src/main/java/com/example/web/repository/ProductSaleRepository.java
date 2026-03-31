package com.example.web.repository;

import com.example.web.entity.ProductSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSaleRepository extends JpaRepository<ProductSale, Long> {
    List<ProductSale> findByProductId(Long productId);

    List<ProductSale> findBySaleId(Long saleId);
}
