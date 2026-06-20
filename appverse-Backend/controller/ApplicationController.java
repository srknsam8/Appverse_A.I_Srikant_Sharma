package com.appverse.controller;

import com.appverse.dto.ApplicationDTO;
import com.appverse.service.ApplicationService;
import lombok.extern.slf4j.Slf4j; // <-- Added Logger Import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // <-- Added Logger Annotation
@RestController
@CrossOrigin(origins = "http://localhost:5173") 
@RequestMapping("/api/apps")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // React will call this to populate the main Marketplace grid
    @GetMapping
    public ResponseEntity<List<ApplicationDTO>> getAllApps() {
        // 1. Log the Entry
        log.info("API Hit: GET /api/apps | Fetching all applications for the marketplace grid");
        
        List<ApplicationDTO> apps = applicationService.getAllApplications();
        
        // 2. Log the Exit
        log.info("API Success: GET /api/apps | Returned {} total applications", apps.size());
        return ResponseEntity.ok(apps);
    }

    // We will use this later for Category filtering!
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ApplicationDTO>> getAppsByCategory(@PathVariable Long categoryId) {
        // 1. Log the Entry
        log.info("API Hit: GET /api/apps/category/{} | Fetching filtered applications", categoryId);
        
        List<ApplicationDTO> apps = applicationService.getApplicationsByCategoryId(categoryId);
        
        // 2. Log the Exit
        log.info("API Success: GET /api/apps/category/{} | Returned {} applications", categoryId, apps.size());
        return ResponseEntity.ok(apps);
    }
    
    // --- NEW: FETCH SINGLE APP DETAILS ---
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        // 1. Log the Entry
        log.info("API Hit: GET /api/apps/{} | Fetching single application details", id);
        
        ApplicationDTO appDTO = applicationService.getApplicationById(id);
        
        // 2. Log the Exit
        log.info("API Success: GET /api/apps/{} | Successfully fetched details for '{}'", id, appDTO.getTitle());
        return ResponseEntity.ok(appDTO);
    }

    // --- NEW: UPLOAD AN APP ---
    @PostMapping("/upload")
    public ResponseEntity<String> uploadApplication(
            @RequestBody ApplicationDTO appDTO, 
            @RequestParam Long developerId) {
        
        // 1. Log the Entry
        log.info("API Hit: POST /api/apps/upload | Developer ID: {} uploading new app: '{}'", developerId, appDTO.getTitle());
        
        applicationService.uploadApplication(appDTO, developerId);
        
        // 2. Log the Exit
        log.info("API Success: POST /api/apps/upload | Application '{}' uploaded successfully", appDTO.getTitle());
        return ResponseEntity.ok("Application uploaded successfully!");
    }

    // --- NEW: The Analytics Endpoint ---
    @PostMapping("/{id}/download")
    public ResponseEntity<?> recordDownload(@PathVariable Long id) {
        // 1. Log the Entry
        log.info("API Hit: POST /api/apps/{}/download | Recording new download", id);
        
        applicationService.incrementDownloadCount(id);
        
        // 2. Log the Exit
        log.info("API Success: POST /api/apps/{}/download | Download recorded successfully", id);
        return ResponseEntity.ok().body("Download recorded successfully");
    }
}