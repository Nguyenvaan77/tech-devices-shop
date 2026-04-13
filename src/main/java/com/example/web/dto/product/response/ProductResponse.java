package com.example.web.dto.product.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.web.dto.productimage.ProductImageDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String specifications;
    private String description;
    private String status;
    private Double ratingAvg;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
    private Long businessOwnerId;
    private List<ProductImageDto> images = new ArrayList<>();
}