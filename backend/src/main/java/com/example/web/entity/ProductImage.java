package com.example.web.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bucketName;

    private String originalFileName;

    private String fileName;

    private long fileSize;

    private String contentType;

    private String publicUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}