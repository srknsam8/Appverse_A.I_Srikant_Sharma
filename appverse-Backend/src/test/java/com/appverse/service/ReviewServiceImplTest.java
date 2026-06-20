package com.appverse.service;

import com.appverse.dto.ReviewDTO;
import com.appverse.entity.Application;
import com.appverse.entity.Review;
import com.appverse.entity.User;
import com.appverse.exception.ResourceNotFoundException;
import com.appverse.exception.UserNotFoundException;
import com.appverse.repository.ApplicationRepository;
import com.appverse.repository.ReviewRepository;
import com.appverse.repository.UserRepository;
import com.appverse.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiService aiService; // <-- Mocking the AI so we don't actually hit an API during tests!

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User customer;
    private Application application;
    private ReviewDTO reviewDTOWithComment;
    private ReviewDTO reviewDTOEmptyComment;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setUsername("TestUser");

        application = new Application();
        application.setId(10L);
        application.setTitle("Test App");

        // Scenario 1: A review with text
        reviewDTOWithComment = new ReviewDTO();
        reviewDTOWithComment.setApplicationId(10L);
        reviewDTOWithComment.setRating(5);
        reviewDTOWithComment.setComment("This app is absolutely amazing!");

        // Scenario 2: A review with NO text (just a star rating)
        reviewDTOEmptyComment = new ReviewDTO();
        reviewDTOEmptyComment.setApplicationId(10L);
        reviewDTOEmptyComment.setRating(4);
        reviewDTOEmptyComment.setComment("   "); // Simulating blank spaces
    }

    // --- TEST 1: The AI Triggers on Real Comments ---
    @Test
    void testAddReview_WithComment_TriggersAiService() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));
        
        // We tell the fake AI to return "POSITIVE" when given this exact string
        when(aiService.analyzeReviewSentiment("This app is absolutely amazing!")).thenReturn("POSITIVE");
        
        // A neat Mockito trick: Tell the repository to just return whatever object was passed into the save() method
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Review savedReview = reviewService.addReview(reviewDTOWithComment, 1L);

        // Assert
        assertNotNull(savedReview);
        assertEquals("POSITIVE", savedReview.getSentiment());
        assertEquals("This app is absolutely amazing!", savedReview.getComment());
        
        // Verify the AI was called exactly ONE time
        verify(aiService, times(1)).analyzeReviewSentiment(anyString());
    }

    // --- TEST 2: The AI is Skipped on Empty Comments ---
    @Test
    void testAddReview_EmptyComment_SkipsAiService() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(application));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Review savedReview = reviewService.addReview(reviewDTOEmptyComment, 1L);

        // Assert
        assertNotNull(savedReview);
        
        // It should default to NEUTRAL as per your business logic
        assertEquals("NEUTRAL", savedReview.getSentiment()); 
        
        // Verify the AI was NEVER called (saving API costs!)
        verify(aiService, never()).analyzeReviewSentiment(anyString());
    }

    // --- TEST 3: Sad Path - User Doesn't Exist ---
    @Test
    void testAddReview_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            reviewService.addReview(reviewDTOWithComment, 99L);
        });

        // Verify we never checked the app, never called AI, and never saved to DB
        verify(applicationRepository, never()).findById(anyLong());
        verify(aiService, never()).analyzeReviewSentiment(anyString());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    // --- TEST 4: Simple Fetch ---
    @Test
    void testGetReviewsForApplication_Success() {
        // Arrange
        Review mockReview = new Review();
        mockReview.setId(100L);
        when(reviewRepository.findByApplicationId(10L)).thenReturn(List.of(mockReview));

        // Act
        List<Review> results = reviewService.getReviewsForApplication(10L);

        // Assert
        assertEquals(1, results.size());
        assertEquals(100L, results.get(0).getId());
    }
}