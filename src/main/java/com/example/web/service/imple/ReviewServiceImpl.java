package com.example.web.service.imple;

import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.dto.review.response.ReviewResponse;
import com.example.web.entity.Product;
import com.example.web.entity.Review;
import com.example.web.entity.User;
import com.example.web.mapper.ReviewMapper;
import com.example.web.repository.ProductRepository;
import com.example.web.repository.ReviewRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewMapper.toEntity(request);

        review.setProduct(product);
        review.setUser(user);

        review = reviewRepository.save(review);

        return reviewMapper.toResponse(review);
    }

    @Override
    public List<ReviewResponse> getProductReviews(Long productId) {

        return reviewRepository.findByProductId(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
}