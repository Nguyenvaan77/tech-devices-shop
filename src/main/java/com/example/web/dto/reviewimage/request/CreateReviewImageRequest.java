package com.example.web.dto.reviewimage.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewImageRequest {
    private String imageUrl;
}
