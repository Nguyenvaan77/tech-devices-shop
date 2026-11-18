package com.example.web.dto.sale.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSaleResponse {
    private Long id;
    private Long productId;
    private Long saleId;
    private Integer value;
    private Boolean isActive;
}
