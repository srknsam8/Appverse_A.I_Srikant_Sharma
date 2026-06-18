package com.appverse.service;

import com.appverse.dto.UserDTO;
import com.appverse.entity.User;
import com.appverse.exception.ResourceAlreadyExistsException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Tells JUnit to use Mockito for fake objects
public class UserServiceImplTest {

    // 1. Create the "Fake" dependencies
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    // 2. Inject the fakes into the REAL service we want to test
    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO testUserDTO;

    // 3. Set up some dummy data before every single test runs
    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setUsername("testUser");
        testUserDTO.setEmail("test@appverse.com");
        testUserDTO.setPassword("Password123!");
    }

    // --- TEST 1: The Happy Path (Registration Works) ---
    @Test
    void testRegisterUser_Success() {
        // Arrange: Tell the fake repository to say "No, this email doesn't exist"
        when(userRepository.existsByEmail(testUserDTO.getEmail())).thenReturn(false);
        // Tell the fake encoder to scramble the password
        when(passwordEncoder.encode(testUserDTO.getPassword())).thenReturn("scrambled123");
        
        // Tell the fake repository what to return when .save() is called
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(testUserDTO.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act: Run your actual logic
        User result = userService.registerUser(testUserDTO);

        // Assert: Prove it worked
        assertNotNull(result);
        assertEquals("test@appverse.com", result.getEmail());
        
        // Prove that the save method was called exactly one time
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- TEST 2: The Error Path (Duplicate Email) ---
    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        // Arrange: Tell the fake repository to say "Yes, this email is taken"
        when(userRepository.existsByEmail(testUserDTO.getEmail())).thenReturn(true);

        // Act & Assert: Prove that your custom exception is thrown
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.registerUser(testUserDTO);
        });
        
        // Prove that the application safely stopped and NEVER tried to save to the database
        verify(userRepository, never()).save(any(User.class));
    }
}