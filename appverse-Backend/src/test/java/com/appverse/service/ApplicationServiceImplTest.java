package com.appverse.service;

import com.appverse.dto.ApplicationDTO;
import com.appverse.entity.Application;
import com.appverse.entity.Category;
import com.appverse.entity.User;
import com.appverse.exception.ResourceNotFoundException;
import com.appverse.exception.UserNotFoundException;
import com.appverse.repository.ApplicationRepository;
import com.appverse.repository.CategoryRepository;
import com.appverse.repository.ReviewRepository;
import com.appverse.repository.UserRepository;
import com.appverse.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceImplTest {

    // 1. Mock ALL the dependencies the Service needs
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    // 2. Inject the mocks into the actual service we are testing
    @InjectMocks
    private ApplicationServiceImpl applicationService;

    // 3. Set up some dummy data for our tests
    private ApplicationDTO applicationDTO;
    private User developer;
    private Category category;
    private Application application;

    @BeforeEach
    void setUp() {
        developer = new User();
        developer.setId(1L);
        developer.setUsername("DevTest");

        category = new Category();
        category.setId(10L);
        category.setName("Productivity");

        applicationDTO = new ApplicationDTO();
        applicationDTO.setTitle("My Test App");
        applicationDTO.setDescription("A great app.");
        applicationDTO.setCategoryId(10L);

        application = new Application();
        application.setId(100L);
        application.setTitle("My Test App");
        application.setDeveloper(developer);
        application.setCategory(category);
        application.setDownloadCount(5);
    }

    // --- HAPPY PATH: Successfully upload an app ---
    @Test
    void testUploadApplication_Success() {
        // Arrange: Tell the mocks how to behave
        when(userRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        // Act: Run the actual method
        Application savedApp = applicationService.uploadApplication(applicationDTO, 1L);

        // Assert: Verify it worked
        assertNotNull(savedApp);
        assertEquals("My Test App", savedApp.getTitle());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    // --- SAD PATH: Trying to upload with a fake developer ID ---
    @Test
    void testUploadApplication_DeveloperNotFound_ThrowsException() {
        // Arrange: Mock the DB returning empty for ID 99
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: Check that the exception stops the process
        assertThrows(UserNotFoundException.class, () -> {
            applicationService.uploadApplication(applicationDTO, 99L);
        });
        
        // Verify we NEVER tried to save the app to the database
        verify(applicationRepository, never()).save(any(Application.class));
    }

    // --- HAPPY PATH: Fetching an App correctly maps the DTO and Download Count ---
    @Test
    void testGetApplicationById_Success() {
        // Arrange
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(reviewRepository.findByApplicationId(100L)).thenReturn(null); // Assuming no reviews for this test

        // Act
        ApplicationDTO result = applicationService.getApplicationById(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(5, result.getDownloadCount());
        assertEquals(0.0, result.getAverageRating());
    }

    // --- HAPPY PATH: Testing the Analytics counter ---
    @Test
    void testIncrementDownloadCount_Success() {
        // Arrange
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        
        // Act
        applicationService.incrementDownloadCount(100L);

        // Assert: It started at 5, it should now be 6!
        assertEquals(6, application.getDownloadCount());
        verify(applicationRepository, times(1)).save(application);
    }
}