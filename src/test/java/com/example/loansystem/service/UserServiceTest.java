package com.example.loansystem.service;

import com.example.loansystem.dto.UserDTO;
import com.example.loansystem.exceptions.BadCredentialsException;
import com.example.loansystem.exceptions.UserAlreadyExistsException;
import com.example.loansystem.model.User;
import com.example.loansystem.model.UserRole;
import com.example.loansystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        // Mocked data
        UserDTO userDTO = new UserDTO();
        userDTO.setUserEmail("test@example.com");
        userDTO.setPassword("password");

        when(userRepository.findByUserEmail(userDTO.getUserEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Test the registerUser method
        userService.registerUser(userDTO);

        // Verify that save method was called on userRepository
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUserUserAlreadyExists() {
        // Mocked data
        UserDTO userDTO = new UserDTO();
        userDTO.setUserEmail("existing@example.com");
        userDTO.setPassword("password");

        when(userRepository.findByUserEmail(userDTO.getUserEmail())).thenReturn(Optional.of(new User()));

        // Test the registerUser method with an existing user
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userDTO));
    }

    @Test
    public void testLoginUser() {
        // Mocked data
        String username = "test@example.com";
        String password = "password";
        String hashedPassword = "hashedPassword";

        User user = new User();
        user.setUserEmail(username);
        user.setPassword(hashedPassword);

        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        // Test the loginUser method
        String authToken = userService.loginUser(username, password);

        // Verify that the method returns a non-null authentication token
        assertNotNull(authToken);
    }

    @Test
    public void testLoginUserInvalidCredentials() {
        // Mocked data
        String username = "test@example.com";
        String password = "password";
        String hashedPassword = "hashedPassword";

        User user = new User();
        user.setUserEmail(username);
        user.setPassword(hashedPassword);

        when(userRepository.findByUserEmail(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // Test the loginUser method with invalid credentials
        assertThrows(BadCredentialsException.class, () -> userService.loginUser(username, password));
    }
}

