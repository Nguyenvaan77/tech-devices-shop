package com.example.web.dto.product.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {
    private String name;
    private String specifications;
    private String description;
    private String status;
    private Long categoryId;
}