package com.example.web.specification;

import com.example.web.entity.Product;
import com.example.web.dto.product.request.ProductFilterRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Product> filterProducts(ProductFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Filter by keyword in name or description
            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String keyword = "%" + filter.getKeyword().toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keyword),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), keyword)));
            }

            // Filter by brand
            if (filter.getBrand() != null && !filter.getBrand().isBlank()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("brand"), filter.getBrand()));
            }

            // Filter by status
            if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            // Filter by category
            if (filter.getCategoryId() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            // Filter by price range
            if (filter.getMinPrice() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // Filter by minimum rating
            if (filter.getMinRating() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("ratingAvg"), filter.getMinRating()));
            }

            return predicate;
        };
    }
}
