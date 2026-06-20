package com.appverse.service;

import com.appverse.dto.LoginDTO;
import com.appverse.dto.UserDTO;
import com.appverse.entity.User;
import com.appverse.exception.InvalidCredentialsException;
import com.appverse.exception.ResourceAlreadyExistsException;
import com.appverse.exception.UserNotFoundException;
import com.appverse.repository.UserRepository;
import com.appverse.security.JwtTokenProvider;
import com.appverse.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO testUserDTO;
    private LoginDTO testLoginDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup for Registration
        testUserDTO = new UserDTO();
        testUserDTO.setUsername("testUser");
        testUserDTO.setEmail("test@appverse.com");
        testUserDTO.setPassword("Password123!");

        // Setup for Login
        testLoginDTO = new LoginDTO();
        testLoginDTO.setEmail("test@appverse.com");
        testLoginDTO.setPassword("Password123!");

        // Setup for Database User Mock
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@appverse.com");
        testUser.setPassword("scrambled123"); // This represents the hashed password in the DB
    }

    // --- TEST 1: Registration Happy Path ---
    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(testUserDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testUserDTO.getPassword())).thenReturn("scrambled123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(testUserDTO);

        assertNotNull(result);
        assertEquals("test@appverse.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- TEST 2: Registration Sad Path (Duplicate Email) ---
    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(testUserDTO.getEmail())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.registerUser(testUserDTO);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }

    // --- TEST 3: Login Happy Path (Success) ---
    @Test
    void testLoginUser_Success() {
        // Arrange
        when(userRepository.findByEmail(testLoginDTO.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testLoginDTO.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(testUser.getEmail())).thenReturn("fake-jwt-token-12345");

        // Act
        String token = userService.loginUser(testLoginDTO);

        // Assert
        assertNotNull(token);
        assertEquals("fake-jwt-token-12345", token);
    }

    // --- TEST 4: Login Sad Path (Email Not Found) ---
    @Test
    void testLoginUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(testLoginDTO.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.loginUser(testLoginDTO);
        });

        // Verify we never checked the password or generated a token
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    // --- TEST 5: Login Sad Path (Wrong Password) ---
    @Test
    void testLoginUser_InvalidPassword_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(testLoginDTO.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testLoginDTO.getPassword(), testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUser(testLoginDTO);
        });

        // Verify we never generated a token
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }
}