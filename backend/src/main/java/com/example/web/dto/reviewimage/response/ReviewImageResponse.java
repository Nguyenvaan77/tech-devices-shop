package com.example.web.dto.reviewimage.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImageResponse {
    private Long id;
    private String bucketName;
    private String originalFileName;
    private String fileName;
    private long fileSize;
    private String contentType;
    private String publicUrl;
    private Long reviewId;
}
