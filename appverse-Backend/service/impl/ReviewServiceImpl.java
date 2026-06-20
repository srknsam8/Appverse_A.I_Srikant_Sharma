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
import lombok.extern.slf4j.Slf4j; // <-- Added Logger Import
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j // <-- Added Logger Annotation
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ApplicationRepository applicationRepository, UserRepository userRepository, AiService aiService) {
        this.reviewRepository = reviewRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
    }

    @Override
    public Review addReview(ReviewDTO reviewDTO, Long customerId) {
        log.info("Attempting to add new review for Application ID: {} by User ID: {}", reviewDTO.getApplicationId(), customerId);

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Failed to add review. User not found with ID: {}", customerId);
                    return new UserNotFoundException("User not found!");
                });

        Application app = applicationRepository.findById(reviewDTO.getApplicationId())
                .orElseThrow(() -> {
                    log.warn("Failed to add review. Application not found with ID: {}", reviewDTO.getApplicationId());
                    return new ResourceNotFoundException("Application not found!");
                });

        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setUser(customer);
        review.setApplication(app);

        // --- THE AI INTEGRATION ---
        if (reviewDTO.getComment() != null && !reviewDTO.getComment().trim().isEmpty()) {
            log.info("Sending review comment to AI Service for sentiment analysis...");
            String sentimentResult = aiService.analyzeReviewSentiment(reviewDTO.getComment());
            log.info("AI Sentiment Analysis completed. Result: {}", sentimentResult);
            review.setSentiment(sentimentResult);
        } else {
            log.info("No comment provided in review. Skipping AI analysis and defaulting to NEUTRAL.");
            review.setSentiment("NEUTRAL");
        }

        Review savedReview = reviewRepository.save(review);
        log.info("Successfully saved review ID: {} with sentiment: {}", savedReview.getId(), savedReview.getSentiment());
        
        return savedReview;
    }

    @Override
    public List<Review> getReviewsForApplication(Long applicationId) {
        log.info("Fetching all reviews for Application ID: {}", applicationId);
        
        List<Review> reviews = reviewRepository.findByApplicationId(applicationId);
        
        log.info("Found {} reviews for Application ID: {}", reviews.size(), applicationId);
        return reviews;
    }
}