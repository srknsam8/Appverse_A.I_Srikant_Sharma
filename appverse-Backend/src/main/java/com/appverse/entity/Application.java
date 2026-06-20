package com.appverse.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    // --- The bucket for our Release Notes! ---
    @Column(columnDefinition = "TEXT")
    private String releaseNotes;

    private Double price;
    
    private String version;

    // --- NEW: The Database Column for Analytics! ---
    @Column(name = "download_count", columnDefinition = "integer default 0")
    private Integer downloadCount = 0;

    private LocalDateTime uploadDate = LocalDateTime.now();

    // 1. Link to the Developer who uploaded it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", nullable = false)
    private User developer;

    // 2. Link to its Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}