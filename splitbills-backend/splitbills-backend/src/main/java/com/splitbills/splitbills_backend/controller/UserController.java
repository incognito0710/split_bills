package com.splitbills.splitbills_backend.controller;

import com.splitbills.splitbills_backend.model.User;
import com.splitbills.splitbills_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get profile of logged-in user
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName(); // email from JWT
        Optional<User> userOpt = userRepository.findByEmail(email);
        return ResponseEntity.ok(userOpt.orElseThrow(() -> new RuntimeException("User not found")));
    }

    // List all users (for demo purposes, optional)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
