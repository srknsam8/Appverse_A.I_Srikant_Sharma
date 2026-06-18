package com.appverse.service.impl;

import com.appverse.dto.ReviewDTO;
import com.appverse.entity.Application;
import com.appverse.entity.Review;
import com.appverse.entity.User;
import com.appverse.exception.ResourceNotFoundException;
import com.appverse.exception.UserNotFoundException;
import com.appverse.repository.ApplicationRepository;
import com.appverse.repository.ReviewRepository;
import com.appverse.repository.UserRepository;
import com.appverse.service.AiService;
import com.appverse.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final AiService aiService; // 1. Bring in the AI Machine

    // 2. Add AiService to the Constructor Injection
    public ReviewServiceImpl(ReviewRepository reviewRepository, ApplicationRepository applicationRepository, UserRepository userRepository, AiService aiService) {
        this.reviewRepository = reviewRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
    }

    @Override
    public Review addReview(ReviewDTO reviewDTO, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        Application app = applicationRepository.findById(reviewDTO.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found!"));

        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setUser(customer);
        review.setApplication(app);

        // --- 3. THE AI INTEGRATION ---
        // Only trigger the AI if the user actually typed a comment
        if (reviewDTO.getComment() != null && !reviewDTO.getComment().trim().isEmpty()) {
            String sentimentResult = aiService.analyzeReviewSentiment(reviewDTO.getComment());
            review.setSentiment(sentimentResult);
        } else {
            review.setSentiment("NEUTRAL"); // Default tag if there is no text
        }

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsForApplication(Long applicationId) {
        return reviewRepository.findByApplicationId(applicationId);
    }
}