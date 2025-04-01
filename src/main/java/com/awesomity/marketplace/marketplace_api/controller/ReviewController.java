package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ApiResponse;
import com.awesomity.marketplace.marketplace_api.dto.ReviewRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Review;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse> postReview(@PathVariable Long productId,
                                                  @Valid @RequestBody ReviewRequestDto dto) {
        try {
            User currentUser = getCurrentUser();
            Review review = reviewService.createReview(dto.getRating(), dto.getComment(), productId, currentUser);
            log.info("Review posted successfully for productId: {}", productId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Review posted successfully", review));
        } catch (ResourceNotFoundException ex) {
            log.error("Review posting failed: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        } catch (BadRequestException ex) {
            log.error("Bad request when posting review: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getReviews(@PathVariable Long productId) {
        try {
            List<Review> reviews = reviewService.getReviewsByProduct(productId);
            log.info("Reviews retrieved for productId: {}", productId);
            return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviews));
        } catch (ResourceNotFoundException ex) {
            log.error("Error retrieving reviews for productId {}: {}", productId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(ex.getMessage()));
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}
