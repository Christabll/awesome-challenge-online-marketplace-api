package com.awesomity.marketplace.marketplace_api.serviceImpl;


import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.entity.Review;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.OrderRepository;
import com.awesomity.marketplace.marketplace_api.repository.ProductRepository;
import com.awesomity.marketplace.marketplace_api.repository.ReviewRepository;
import com.awesomity.marketplace.marketplace_api.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateReviewIfUserHasOrderedProduct() {
        Long productId = 1L;
        User user = new User(); user.setId(2L);
        Product product = new Product();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.existsOrderByUserAndProduct(user.getId(), productId)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review savedReview = reviewService.createReview(5, "Great product!", productId, user);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getRating()).isEqualTo(5);
        assertThat(savedReview.getComment()).isEqualTo("Great product!");
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldThrowWhenUserDidNotOrderProduct() {
        Long productId = 1L;
        User user = new User(); user.setId(3L);
        Product product = new Product();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.existsOrderByUserAndProduct(user.getId(), productId)).thenReturn(false);

        assertThrows(BadRequestException.class, () ->
                reviewService.createReview(4, "Nice", productId, user)
        );

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldReturnReviewsForProduct() {
        Long productId = 1L;
        Product product = new Product();
        List<Review> mockReviews = List.of(new Review(), new Review());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.findByProduct(product)).thenReturn(mockReviews);

        List<Review> result = reviewService.getReviewsByProduct(productId);

        assertThat(result).hasSize(2);
        verify(reviewRepository).findByProduct(product);
    }

    @Test
    void shouldThrowWhenNoReviewsFound() {
        Long productId = 1L;
        Product product = new Product();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.findByProduct(product)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.getReviewsByProduct(productId)
        );
    }
}
