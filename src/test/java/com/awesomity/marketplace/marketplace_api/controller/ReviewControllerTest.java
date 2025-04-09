package com.awesomity.marketplace.marketplace_api.controller;

import com.awesomity.marketplace.marketplace_api.dto.ReviewRequestDto;
import com.awesomity.marketplace.marketplace_api.entity.Review;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.security.CustomUserDetails;
import com.awesomity.marketplace.marketplace_api.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @Mock private ReviewService reviewService;
    @Mock private Authentication authentication;
    @Mock private CustomUserDetails customUserDetails;

    @InjectMocks
    private ReviewController controller;

    private final User testUser = new User();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser.setId(1L);
        when(customUserDetails.getUser()).thenReturn(testUser);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldPostReviewSuccessfully() {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setRating(5);
        dto.setComment("Excellent product");

        Review review = new Review(); review.setId(1L);

        when(reviewService.createReview(5, "Excellent product", 1L, testUser)).thenReturn(review);

        ResponseEntity<?> response = controller.postReview(1L, dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(((Review) ((com.awesomity.marketplace.marketplace_api.dto.ApiResponse<?>) response.getBody()).getData()).getId()).isEqualTo(1L);
    }

    @Test
    void shouldGetReviewsSuccessfully() {
        Review r1 = new Review(); r1.setId(1L);
        Review r2 = new Review(); r2.setId(2L);

        when(reviewService.getReviewsByProduct(1L)).thenReturn(List.of(r1, r2));

        var response = controller.getReviews(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(((List<?>) response.getBody().getData()).size()).isEqualTo(2);
    }
}