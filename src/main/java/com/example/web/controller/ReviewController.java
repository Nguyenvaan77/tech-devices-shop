package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.service.inter.ReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{userId}")
    public Object createReview(
            @PathVariable Long userId,
            @RequestBody CreateReviewRequest request
    ) {

        return ApiResponse.created(
                reviewService.createReview(userId, request)
        );
    }

    @GetMapping("/products/{productId}")
    public Object getProductReviews(@PathVariable Long productId) {

        return ApiResponse.success(
                reviewService.getProductReviews(productId)
        );
    }
}