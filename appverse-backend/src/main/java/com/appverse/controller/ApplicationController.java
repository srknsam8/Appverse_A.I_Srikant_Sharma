package com.appverse.controller;

import com.appverse.dto.ApplicationDTO;
import com.appverse.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // We will use this later for Category filtering!
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ApplicationDTO>> getAppsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(applicationService.getApplicationsByCategoryId(categoryId));
    }
    
    // --- NEW: FETCH SINGLE APP DETAILS ---
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        ApplicationDTO appDTO = applicationService.getApplicationById(id);
        return ResponseEntity.ok(appDTO);
    }

    // --- NEW: UPLOAD AN APP ---
    @PostMapping("/upload")
    public ResponseEntity<String> uploadApplication(
            @RequestBody ApplicationDTO appDTO, 
            @RequestParam Long developerId) {
        
        applicationService.uploadApplication(appDTO, developerId);
        return ResponseEntity.ok("Application uploaded successfully!");
    }
 // --- NEW: The Analytics Endpoint ---
    @PostMapping("/{id}/download")
    public ResponseEntity<?> recordDownload(@PathVariable Long id) {
        applicationService.incrementDownloadCount(id);
        return ResponseEntity.ok().body("Download recorded successfully");
    }
    
}