package com.example.web.dto.review.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    private Long productId;
    private Integer rating;
    private String comment;

}