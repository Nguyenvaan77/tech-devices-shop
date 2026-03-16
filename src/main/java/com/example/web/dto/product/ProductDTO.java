package com.example.web.dto.product;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private Double ratingAvg;
    private Integer ratingCount;

}