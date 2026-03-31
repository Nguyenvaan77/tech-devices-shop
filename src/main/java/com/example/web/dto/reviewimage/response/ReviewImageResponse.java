package com.example.web.dto.reviewimage.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImageResponse {
    private Long id;
    private String imageUrl;
    private Long reviewId;
}
