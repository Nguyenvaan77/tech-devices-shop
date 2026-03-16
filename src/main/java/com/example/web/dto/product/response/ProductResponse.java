package com.example.web.dto.product.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private Double ratingAvg;
    private Integer ratingCount;
}