package com.example.web.dto.productvideo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVideoDTO {
    private Long id;
    private String bucketName;
    private String originalFileName;
    private String fileName;
    private long fileSize;
    private String contentType;
    private String publicUrl;
    private Long productId;
}
