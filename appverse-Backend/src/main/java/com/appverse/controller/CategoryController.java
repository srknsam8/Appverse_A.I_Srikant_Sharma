package com.appverse.controller;

import com.appverse.entity.Category;
import com.appverse.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j; // <-- Added Logger Import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j // <-- Added Logger Annotation
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // POST -> Create a new category
    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        // 1. Log the Entry
        log.info("API Hit: POST /api/categories/add | Attempting to create a new category");
        
        Category savedCategory = categoryRepository.save(category);
        
        // 2. Log the Exit
        log.info("API Success: POST /api/categories/add | Successfully created Category ID: {}", savedCategory.getId());
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }
}