package com.example.web.service.inter;

import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.dto.review.request.UpdateReviewRequest;
import com.example.web.dto.review.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long productId, CreateReviewRequest request);

    List<ReviewResponse> getProductReviews(Long productId);

    ReviewResponse updateReview(Long id, UpdateReviewRequest request);

    void deleteReview(Long id);
}
