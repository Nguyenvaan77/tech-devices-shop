package com.example.web.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product filter and search request with pagination")
public class ProductFilterRequest {
    @Schema(description = "Search keyword in product name or description", example = "laptop")
    private String keyword;

    @Schema(description = "Filter by brand", example = "Dell")
    private String brand;

    @Schema(description = "Filter by product status", example = "ACTIVE")
    private String status;

    @Schema(description = "Filter by category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Minimum price filter", example = "500.00")
    private BigDecimal minPrice;

    @Schema(description = "Maximum price filter", example = "2000.00")
    private BigDecimal maxPrice;

    @Schema(description = "Minimum rating filter", example = "4.0")
    private Double minRating;

    @Schema(description = "Page number (0-indexed)", example = "0")
    private Integer page;

    @Schema(description = "Items per page", example = "10")
    private Integer pageSize;

    @Schema(description = "Field to sort by", example = "price")
    private String sortBy;

    @Schema(description = "Sort direction (ASC or DESC)", example = "DESC")
    private String sortDirection;
}
