package com.appverse.controller;

import com.appverse.dto.LoginDTO;
import com.appverse.dto.UserDTO;
import com.appverse.entity.User;
import com.appverse.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // <-- Added Logger Import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j // <-- Added Logger Annotation
@RestController 
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/users") 
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register") 
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDTO userDTO) {
        // 1. Log the Entry
        log.info("API Hit: POST /register | Attempting to register email: {}", userDTO.getEmail());

        User savedUser = userService.registerUser(userDTO);
        
        // 2. Log the Exit
        log.info("API Success: POST /register | Successfully created user ID: {}", savedUser.getId());
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // --- UPDATED LOGIN ENDPOINT ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        // 1. Log the Entry (Remember: NEVER log the password!)
        log.info("API Hit: POST /login | Authentication requested for email: {}", loginDTO.getEmail());

        // 2. Pass the credentials to our Service Layer to get the ID Card
        String token = userService.loginUser(loginDTO);
        
        // 3. Fetch the actual user from the database to get their role
        User user = userService.getUserByEmail(loginDTO.getEmail());
        
        // 4. Wrap the token AND the user details inside a clean JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole()); // <-- The golden ticket!
        response.put("username", user.getUsername());
        
        // 5. Log the Exit
        log.info("API Success: POST /login | Token generated and payload returned for email: {}", loginDTO.getEmail());
        
        // 6. Return a 200 OK status with the full payload
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}