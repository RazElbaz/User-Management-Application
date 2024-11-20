package com.example.usermanagement.controller;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.security.TokenService;
import com.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import com.example.usermanagement.service.RateLimiterService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api")
@Validated
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private TokenService tokenProvider;

    // Handles user login and generates a JWT token
    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestParam @NotBlank @Email String email,
            @RequestParam @NotBlank String password) {

        // check if the rate limit is exceeded
        if (!rateLimiterService.isRequestAllowed()) {
            logger.warn("Rate limit exceeded for email: {}", email);
            return ResponseEntity.status(429).body("Too many login attempts. Please try again later.");
        }

        // authenticate user and generate token
        User user = userService.login(email, password);
        String token = tokenProvider.generateToken(user);

        // return the token in the response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    // Registers a new user and returns the created user object
    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody @Valid User user) {
        User createdUser = userService.signUp(user);
        return ResponseEntity.ok(createdUser);
    }

    // Fetches all users from the database
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Updates the status of a specific user
    @PutMapping("/updateStatus")
    public ResponseEntity<Object> updateStatus(
            @RequestParam @NotNull Long id,
            @RequestParam @NotBlank String status) {

        User updateUser = userService.updateStatus(id, status);
        return ResponseEntity.ok(updateUser);
    }

    // Deletes a user by ID and returns a confirmation message
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam @NotNull Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

}
