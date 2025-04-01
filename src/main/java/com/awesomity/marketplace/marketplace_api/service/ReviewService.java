package com.awesomity.marketplace.marketplace_api.service;

import com.awesomity.marketplace.marketplace_api.entity.Review;
import com.awesomity.marketplace.marketplace_api.entity.User;
import java.util.List;

public interface ReviewService {
    Review createReview(int rating, String comment, Long productId, User user);
    List<Review> getReviewsByProduct(Long productId);
}
