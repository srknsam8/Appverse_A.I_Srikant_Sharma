package com.appverse.service.impl;

import com.appverse.dto.LoginDTO;
import com.appverse.dto.UserDTO;
import com.appverse.entity.User;
import com.appverse.enums.Role;
import com.appverse.exception.InvalidCredentialsException;
import com.appverse.exception.ResourceAlreadyExistsException;
import com.appverse.exception.UserNotFoundException;
import com.appverse.repository.UserRepository;
import com.appverse.security.JwtTokenProvider;
import com.appverse.service.UserService;
import lombok.extern.slf4j.Slf4j; // <-- Added Logger Import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j // <-- Added Logger Annotation
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        log.info("Attempting to register new user with email: {}", userDTO.getEmail());

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Registration failed. Email already exists in database: {}", userDTO.getEmail());
            throw new ResourceAlreadyExistsException("Email is already registered!");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        
        // We ignore the DTO and force every new account to be a standard USER
        user.setRole(Role.USER); 
        
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); 

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
        return savedUser;
    }

    @Override
    public String loginUser(LoginDTO loginDTO) {
        log.info("Login attempt initiated for email: {}", loginDTO.getEmail());

        // Step A: Find the user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed. No user found with email: {}", loginDTO.getEmail());
                    return new UserNotFoundException("User not found with email: " + loginDTO.getEmail());
                });

        // Step B: Compare the typed password with the scrambled database password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("Login failed. Invalid password provided for email: {}", loginDTO.getEmail());
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        // Step C: Passwords match! Return the ID Card (JWT Token)
        log.info("User successfully authenticated: {}", loginDTO.getEmail());
        return jwtTokenProvider.generateToken(user.getEmail());
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("Fetching user details for email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Failed to fetch user details. Email not found: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
    }
}