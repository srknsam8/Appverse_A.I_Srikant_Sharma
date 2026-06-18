package com.appverse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationDTO {
    private Long id;

    @NotBlank(message = "App title is required")
    private String title;

    private String description;
    
    // --- The bucket for our Release Notes! ---
    private String releaseNotes;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;
    
    @NotBlank(message = "Version is required")
    private String version;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    // The bucket for the calculated star rating!
    private Double averageRating;

    // --- NEW: The bucket to send the download count to React! ---
    private Integer downloadCount;
}