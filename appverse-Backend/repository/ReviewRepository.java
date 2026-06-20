package com.appverse.repository;

import com.appverse.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Custom method to grab all reviews for a specific app
    List<Review> findByApplicationId(Long applicationId);
}