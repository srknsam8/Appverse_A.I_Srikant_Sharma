package com.appverse.controller;

import com.appverse.dto.ReviewDTO;
import com.appverse.entity.Review;
import com.appverse.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // POST -> Add a new review [cite: 134]
    @PostMapping("/add")
    public ResponseEntity<Review> addReview(
            @Valid @RequestBody ReviewDTO reviewDTO,
            @RequestParam Long customerId) {
            
        Review savedReview = reviewService.addReview(reviewDTO, customerId);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    // GET -> Retrieve all reviews for a specific app [cite: 133]
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Review>> getReviewsForApplication(@PathVariable Long applicationId) {
        List<Review> reviews = reviewService.getReviewsForApplication(applicationId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}