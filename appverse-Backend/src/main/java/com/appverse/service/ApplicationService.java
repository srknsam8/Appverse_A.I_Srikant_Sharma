package com.appverse.service;

import com.appverse.dto.ApplicationDTO;
import com.appverse.entity.Application;
import java.util.List;

public interface ApplicationService {
    Application uploadApplication(ApplicationDTO applicationDTO, Long developerId);
    
    // --- UPDATED METHODS ---
    List<ApplicationDTO> getAllApplications(); 
    List<ApplicationDTO> getApplicationsByCategoryId(Long categoryId); 
    ApplicationDTO getApplicationById(Long id); 

    // --- NEW: The Download Counter Contract ---
    void incrementDownloadCount(Long id);
}