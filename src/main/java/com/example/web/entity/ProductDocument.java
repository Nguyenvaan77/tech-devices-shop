package com.example.web.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDocument {
    private String id;
    // ===== SEARCH =====
    private String name;
    private String description;
    private List<String> tags = new ArrayList<String>();

    // ===== FILTER =====
    private String category;
    private String brand;

    // ===== SORT =====
    private Double price;
    private Integer quantity;
    private Long createdAt;

    // ===== RANKING =====
    private Double rating;
    private Integer sold;
}
