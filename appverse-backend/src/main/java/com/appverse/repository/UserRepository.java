package com.appverse.repository;

import com.appverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // We add these two custom methods so we can check for duplicate emails
    // and log users in later!
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}