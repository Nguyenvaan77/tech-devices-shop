package com.example.web.dto.review.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewRequest {
    private Integer rating;
    private String comment;
}
