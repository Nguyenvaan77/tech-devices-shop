package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.dto.review.response.ReviewResponse;
import com.example.web.service.inter.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product review management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{userId}")
    @Operation(summary = "Create product review", description = "Create a new review for a product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Review created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid review data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or product not found")
    })
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long userId,
            @RequestBody CreateReviewRequest request) {
        ReviewResponse review = reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(review));
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Get product reviews", description = "Retrieve all reviews for a specific product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid product ID"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(@PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}