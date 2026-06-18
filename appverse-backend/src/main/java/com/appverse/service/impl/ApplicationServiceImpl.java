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
import org.springframework.stereotype.Service;

import java.util.List;

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
        User developer = userRepository.findById(developerId)
                .orElseThrow(() -> new UserNotFoundException("Developer not found!"));

        Category category = categoryRepository.findById(applicationDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        Application app = new Application();
        app.setTitle(applicationDTO.getTitle());
        app.setDescription(applicationDTO.getDescription());
        
        // --- Map the release notes from the frontend to the database ---
        app.setReleaseNotes(applicationDTO.getReleaseNotes());
        
        app.setPrice(applicationDTO.getPrice());
        app.setVersion(applicationDTO.getVersion());
        app.setDeveloper(developer);
        app.setCategory(category);

        return applicationRepository.save(app);
    }

    @Override
    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList(); 
    }

    @Override
    public List<ApplicationDTO> getApplicationsByCategoryId(Long categoryId) {
        return applicationRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ApplicationDTO getApplicationById(Long id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found!"));
        return mapToDTO(app);
    }

    // --- NEW: The Download Counter Engine ---
    @Override
    public void incrementDownloadCount(Long id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found!"));
        
        int currentCount = app.getDownloadCount() != null ? app.getDownloadCount() : 0;
        app.setDownloadCount(currentCount + 1);
        
        applicationRepository.save(app);
    }

    // --- HELPER METHOD TO SATISFY DTO REQUIREMENTS ---
    private ApplicationDTO mapToDTO(Application app) {
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
        } else {
            dto.setAverageRating(0.0);
        }

        return dto;
    }
}