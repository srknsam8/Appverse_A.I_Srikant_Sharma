package com.appverse.controller;

import com.appverse.dto.LoginDTO;
import com.appverse.dto.UserDTO;
import com.appverse.entity.User;
import com.appverse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        User savedUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // --- NEW LOGIN ENDPOINT ---
 // --- UPDATED LOGIN ENDPOINT ---
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        // 1. Pass the credentials to our Service Layer to get the ID Card
        String token = userService.loginUser(loginDTO);
        
        // 2. Fetch the actual user from the database to get their role
        // (Assuming you have a method to find a user by their email)
        User user = userService.getUserByEmail(loginDTO.getEmail());
        
        // 3. Wrap the token AND the user details inside a clean JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole()); // <-- The golden ticket!
        response.put("username", user.getUsername());
        
        // 4. Return a 200 OK status with the full payload
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}