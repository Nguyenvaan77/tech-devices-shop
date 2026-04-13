package com.example.web.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.web.dto.productimage.ProductImageDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    private String name;
    private Long businessOwnerId;
    private BigDecimal price;
    private Double ratingAvg;
    private Integer ratingCount;
    private Long categoryId;
    private List<ProductImageDto> images = new ArrayList<>();
}