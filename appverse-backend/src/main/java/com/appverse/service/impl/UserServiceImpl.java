package com.appverse.service.impl;

import com.appverse.dto.LoginDTO;
import com.appverse.dto.UserDTO;
import com.appverse.entity.User;
import com.appverse.enums.Role; // <-- Added this import!
import com.appverse.exception.InvalidCredentialsException;
import com.appverse.exception.ResourceAlreadyExistsException;
import com.appverse.exception.UserNotFoundException;
import com.appverse.repository.UserRepository;
import com.appverse.security.JwtTokenProvider;
import com.appverse.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already registered!");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        
        // --- THE MAGIC FIX ---
        // We ignore the DTO and force every new account to be a standard USER
        user.setRole(Role.USER); 
        
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); 

        return userRepository.save(user);
    }

    @Override
    public String loginUser(LoginDTO loginDTO) {
        // Step A: Find the user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + loginDTO.getEmail()));

        // Step B: Compare the typed password with the scrambled database password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        // Step C: Passwords match! Return the ID Card (JWT Token)
        return jwtTokenProvider.generateToken(user.getEmail());
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}