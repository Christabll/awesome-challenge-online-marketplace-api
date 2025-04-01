package com.awesomity.marketplace.marketplace_api.service.impl;

import com.awesomity.marketplace.marketplace_api.entity.Product;
import com.awesomity.marketplace.marketplace_api.entity.Review;
import com.awesomity.marketplace.marketplace_api.entity.User;
import com.awesomity.marketplace.marketplace_api.exception.BadRequestException;
import com.awesomity.marketplace.marketplace_api.exception.ResourceNotFoundException;
import com.awesomity.marketplace.marketplace_api.repository.OrderRepository;
import com.awesomity.marketplace.marketplace_api.repository.ProductRepository;
import com.awesomity.marketplace.marketplace_api.repository.ReviewRepository;
import com.awesomity.marketplace.marketplace_api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public Review createReview(int rating, String comment, Long productId, User user) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        boolean hasOrdered = orderRepository.existsOrderByUserAndProduct(user.getId(), productId);
        if (!hasOrdered) {
            throw new BadRequestException("You cannot review a product you have not purchased.");
        }

        // Create and save the review
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);
        review.setProduct(product);

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return reviewRepository.findByProduct(product);
    }
}
