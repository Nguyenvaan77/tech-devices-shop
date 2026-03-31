package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.dto.review.request.UpdateReviewRequest;
import com.example.web.dto.review.response.ReviewResponse;
import com.example.web.dto.reviewimage.request.CreateReviewImageRequest;
import com.example.web.dto.reviewimage.response.ReviewImageResponse;
import com.example.web.service.inter.ReviewService;
import com.example.web.service.inter.ReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAll(@PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> create(
            @PathVariable Long productId,
            @RequestBody CreateReviewRequest request) {
        ReviewResponse review = reviewService.createReview(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(review));
    }
}

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
class ReviewDetailController {

    private final ReviewService reviewService;

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(
            @PathVariable Long id,
            @RequestBody UpdateReviewRequest request) {
        ReviewResponse review = reviewService.updateReview(id, request);
        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}

@RestController
@RequestMapping("/api/reviews/{reviewId}/images")
@RequiredArgsConstructor
class ReviewImageController {

    private final ReviewImageService reviewImageService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewImageResponse>> upload(
            @PathVariable Long reviewId,
            @RequestBody CreateReviewImageRequest request) {
        ReviewImageResponse response = reviewImageService.create(reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}

@RestController
@RequestMapping("/api/review-images")
@RequiredArgsConstructor
class ReviewImageDetailController {

    private final ReviewImageService reviewImageService;

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reviewImageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
