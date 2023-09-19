package com.example.loansystem.service;

import com.example.loansystem.dto.UserDTO;
import com.example.loansystem.exceptions.BadCredentialsException;
import com.example.loansystem.exceptions.UserAlreadyExistsException;
import com.example.loansystem.model.UserRole;
import com.example.loansystem.model.User;
import com.example.loansystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO userDTO) {
        // Check if the username already exists
        if (userRepository.findByUserEmail(userDTO.getUserEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        // Create a new user
        User user = new User();
        user.setUserEmail(userDTO.getUserEmail());
        user.setUserRole(UserRole.BORROWER);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Hash and store the password securely

        userRepository.save(user);
    }

    public String loginUser(String username, String password) {
        // Find the user by username
        User user = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Check if the provided password matches the stored password (after hashing)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return "Authentication_Token";
    }
}