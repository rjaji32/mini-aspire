package com.example.loansystem.controller;

import com.example.loansystem.dto.UserDTO;
import com.example.loansystem.dto.LoginRequest;
import com.example.loansystem.exceptions.UserAlreadyExistsException;
import com.example.loansystem.exceptions.BadCredentialsException;
import com.example.loansystem.service.UserService;
import com.example.loansystem.controller.UserController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    public UserControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        // Prepare a UserDTO for registration
        UserDTO userDTO = new UserDTO();
        userDTO.setUserEmail("test@example.com");
        userDTO.setPassword("password");

        // Mock the userService to return success
        doNothing().when(userService).registerUser(userDTO);

        // Call the controller method
        ResponseEntity<String> response = userController.registerUser(userDTO);

        // Assert that the response is a success (HTTP Status 201)
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        // Prepare a UserDTO for registration
        UserDTO userDTO = new UserDTO();
        userDTO.setUserEmail("existing@example.com");
        userDTO.setPassword("password");

        // Mock the userService to throw UserAlreadyExistsException
        doThrow(UserAlreadyExistsException.class).when(userService).registerUser(userDTO);

        // Call the controller method
        ResponseEntity<String> response = userController.registerUser(userDTO);

        // Assert that the response indicates a conflict (HTTP Status 400)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    public void testLoginUser_Success() {
        // Prepare a LoginRequest for login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test@example.com");
        loginRequest.setPassword("password");

        // Mock the userService to return a token
        when(userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword())).thenReturn("valid-token");

        // Call the controller method
        ResponseEntity<String> response = userController.loginUser(loginRequest);

        // Assert that the response contains a valid token and has HTTP Status 200 (OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("valid-token", response.getBody());
    }

    @Test
    public void testLoginUser_BadCredentials() {
        // Prepare a LoginRequest with invalid credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent@example.com");
        loginRequest.setPassword("invalid-password");

        // Mock the userService to throw BadCredentialsException
        when(userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword())).thenThrow(BadCredentialsException.class);

        // Call the controller method
        ResponseEntity<String> response = userController.loginUser(loginRequest);

        // Assert that the response indicates unauthorized (HTTP Status 401)
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }
}
