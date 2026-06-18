package com.appverse.service;

import com.appverse.dto.ReviewDTO;
import com.appverse.entity.Review;
import java.util.List;

public interface ReviewService {
    Review addReview(ReviewDTO reviewDTO, Long customerId);
    List<Review> getReviewsForApplication(Long applicationId);
}