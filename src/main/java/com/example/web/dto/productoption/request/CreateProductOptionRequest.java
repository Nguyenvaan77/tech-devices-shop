package com.example.web.dto.productoption.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductOptionRequest {
    private Long productItemAttributeTypeId;
    private String valueOption;
    private Integer quantityInStock;
}
