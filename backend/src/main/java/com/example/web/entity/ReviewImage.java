package com.example.web.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage {

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
    @JoinColumn(name = "review_id")
    private Review review;
}