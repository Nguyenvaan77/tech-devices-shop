package com.example.web.dto.reviewimage.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewImageRequest {
    private String bucketName;
    private String originalFileName;
    private String fileName;
    private long fileSize;
    private String contentType;
    private String publicUrl;
}
