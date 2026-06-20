package com.appverse.controller;

import com.appverse.dto.ReviewDTO;
import com.appverse.entity.Review;
import com.appverse.service.ReviewService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // <-- Added Logger Import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // <-- Added Logger Annotation
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // POST -> Add a new review
    @PostMapping("/add")
    public ResponseEntity<Review> addReview(
            @Valid @RequestBody ReviewDTO reviewDTO,
            @RequestParam Long customerId) {
            
        // 1. Log the Entry
        log.info("API Hit: POST /add | User ID: {} adding review for Application ID: {}", customerId, reviewDTO.getApplicationId());
            
        Review savedReview = reviewService.addReview(reviewDTO, customerId);
        
        // 2. Log the Exit
        log.info("API Success: POST /add | Successfully created Review ID: {}", savedReview.getId());
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    // GET -> Retrieve all reviews for a specific app
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Review>> getReviewsForApplication(@PathVariable Long applicationId) {
        
        // 1. Log the Entry
        log.info("API Hit: GET /application/{} | Fetching reviews", applicationId);
        
        List<Review> reviews = reviewService.getReviewsForApplication(applicationId);
        
        // 2. Log the Exit (Logging the size here is great for debugging the React UI!)
        log.info("API Success: GET /application/{} | Returned {} reviews", applicationId, reviews.size());
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}