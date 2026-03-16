package com.example.web.dto.product.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private Long categoryId;

}