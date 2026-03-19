package com.example.web.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product creation request")
public class CreateProductRequest {

    @Schema(description = "Product name", example = "Dell XPS 13", required = true)
    private String name;

    @Schema(description = "Brand name", example = "Dell", required = false)
    private String brand;

    @Schema(description = "Product description", example = "High-performance laptop for professionals", required = false)
    private String description;

    @Schema(description = "Product price", example = "1299.99", required = true)
    private BigDecimal price;

    @Schema(description = "Category ID", example = "1", required = false)
    private Long categoryId;

}