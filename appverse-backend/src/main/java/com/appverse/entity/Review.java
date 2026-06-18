package com.appverse.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating; // e.g., 1 to 5 stars

    @Column(length = 1000)
    private String comment;
    private String sentiment;
    

    private LocalDateTime reviewDate = LocalDateTime.now();

    // 1. Link to the Customer who wrote the review
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 2. Link to the Application being reviewed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
}