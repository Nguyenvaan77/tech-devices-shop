package com.example.web.service.inter;

import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.dto.review.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long userId, CreateReviewRequest request);

    List<ReviewResponse> getProductReviews(Long productId);

}