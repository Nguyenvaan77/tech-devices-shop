package com.example.web.dto.sale.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductSaleRequest {
    private Long productId;
    private Long saleId;
    private Integer value;
}
