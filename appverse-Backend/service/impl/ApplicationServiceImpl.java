package com.appverse.service.impl;

import com.appverse.dto.ApplicationDTO;
import com.appverse.entity.Application;
import com.appverse.entity.Category;
import com.appverse.entity.Review; 
import com.appverse.entity.User;
import com.appverse.exception.ResourceNotFoundException;
import com.appverse.exception.UserNotFoundException;
import com.appverse.repository.ApplicationRepository;
import com.appverse.repository.CategoryRepository;
import com.appverse.repository.ReviewRepository; 
import com.appverse.repository.UserRepository;
import com.appverse.service.ApplicationService;
import lombok.extern.slf4j.Slf4j; // <-- Added Logger Import
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j // <-- Added Logger Annotation
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository; 

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, 
                                  CategoryRepository categoryRepository, 
                                  UserRepository userRepository,
                                  ReviewRepository reviewRepository) { 
        this.applicationRepository = applicationRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Application uploadApplication(ApplicationDTO applicationDTO, Long developerId) {
        log.info("Attempting to upload new application '{}' for Developer ID: {}", applicationDTO.getTitle(), developerId);

        User developer = userRepository.findById(developerId)
                .orElseThrow(() -> {
                    log.warn("Upload failed. Developer not found with ID: {}", developerId);
                    return new UserNotFoundException("Developer not found!");
                });

        Category category = categoryRepository.findById(applicationDTO.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Upload failed. Category not found with ID: {}", applicationDTO.getCategoryId());
                    return new ResourceNotFoundException("Category not found!");
                });

        Application app = new Application();
        app.setTitle(applicationDTO.getTitle());
        app.setDescription(applicationDTO.getDescription());
        
        // --- Map the release notes from the frontend to the database ---
        app.setReleaseNotes(applicationDTO.getReleaseNotes());
        
        app.setPrice(applicationDTO.getPrice());
        app.setVersion(applicationDTO.getVersion());
        app.setDeveloper(developer);
        app.setCategory(category);

        Application savedApp = applicationRepository.save(app);
        log.info("Successfully uploaded application '{}' with new ID: {}", savedApp.getTitle(), savedApp.getId());
        return savedApp;
    }

    @Override
    public List<ApplicationDTO> getAllApplications() {
        log.info("Fetching all applications from the database");
        
        List<Application> apps = applicationRepository.findAll();
        log.info("Found {} total applications. Mapping to DTOs...", apps.size());
        
        return apps.stream()
                .map(this::mapToDTO)
                .toList(); 
    }

    @Override
    public List<ApplicationDTO> getApplicationsByCategoryId(Long categoryId) {
        log.info("Fetching applications for Category ID: {}", categoryId);
        
        List<Application> apps = applicationRepository.findByCategoryId(categoryId);
        log.info("Found {} applications for Category ID: {}", apps.size(), categoryId);
        
        return apps.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ApplicationDTO getApplicationById(Long id) {
        log.info("Fetching details for Application ID: {}", id);
        
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to fetch. Application not found with ID: {}", id);
                    return new ResourceNotFoundException("Application not found!");
                });
                
        return mapToDTO(app);
    }

    // --- NEW: The Download Counter Engine ---
    @Override
    public void incrementDownloadCount(Long id) {
        log.info("Incrementing download count for Application ID: {}", id);
        
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to increment download count. Application not found with ID: {}", id);
                    return new ResourceNotFoundException("Application not found!");
                });
        
        int currentCount = app.getDownloadCount() != null ? app.getDownloadCount() : 0;
        app.setDownloadCount(currentCount + 1);
        
        applicationRepository.save(app);
        log.info("Successfully incremented download count for Application ID: {}. New Count: {}", id, app.getDownloadCount());
    }

    // --- HELPER METHOD TO SATISFY DTO REQUIREMENTS ---
    private ApplicationDTO mapToDTO(Application app) {
        log.debug("Mapping Application entity ID: {} to DTO", app.getId()); // Using DEBUG here as this runs often
        
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setTitle(app.getTitle());
        dto.setDescription(app.getDescription());
        
        // --- Map the release notes from the database back to the frontend ---
        dto.setReleaseNotes(app.getReleaseNotes());
        
        dto.setPrice(app.getPrice());
        dto.setVersion(app.getVersion());
        
        // --- NEW: Package the download count for React! ---
        dto.setDownloadCount(app.getDownloadCount() != null ? app.getDownloadCount() : 0);
        
        if (app.getCategory() != null) {
            dto.setCategoryId(app.getCategory().getId());
        }

        // --- THE MAGIC MATH ---
        List<Review> reviews = reviewRepository.findByApplicationId(app.getId());

        if (reviews != null && !reviews.isEmpty()) {
            double average = reviews.stream()
                    .mapToDouble(review -> review.getRating()) 
                    .average()
                    .orElse(0.0);
            dto.setAverageRating(average);
            log.debug("Calculated average rating for Application ID: {} is {}", app.getId(), average);
        } else {
            dto.setAverageRating(0.0);
            log.debug("No reviews found for Application ID: {}. Setting average rating to 0.0", app.getId());
        }

        return dto;
    }
}