package com.appverse.repository;

import com.appverse.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // Custom methods to easily find apps by category or developer
    List<Application> findByCategoryId(Long categoryId);
    List<Application> findByDeveloperId(Long developerId);
}