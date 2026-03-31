package com.example.web.dto.productimage.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductImageRequest {
    private String imageUrl;
    private Boolean isMain = false;
}
