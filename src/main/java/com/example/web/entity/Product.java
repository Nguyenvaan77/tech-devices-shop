package com.example.web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products", indexes = {
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_business_owner", columnList = "business_owner_id"),
    @Index(name = "idx_product_status_qty", columnList = "is_deleted, quantity_in_stock"),
    @Index(name = "idx_product_status_price", columnList = "is_deleted, price")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private User businessOwner;

    @Column(columnDefinition = "JSON")
    private String specifications;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status;

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer quantityInStock = 0;

    @Column(columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private Double ratingAvg = null;

    @Builder.Default
    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVideo> videos;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSale> sales;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}