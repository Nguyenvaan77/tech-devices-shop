package com.example.web.dto.productimage.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductImageRequest {
    private String bucketName;
    private String originalFileName;
    private String fileName;
    private long fileSize;
    private String contentType;
    private String publicUrl;
    private Long productId;
}
