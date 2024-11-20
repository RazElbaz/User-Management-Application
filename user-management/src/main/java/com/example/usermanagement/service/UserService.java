package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Caching;

import org.springframework.stereotype.Service;
import com.example.usermanagement.enums.Status;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Validates password complexity using regex
    private boolean isValidPassword(String password) {
        // Password validation regex (at least 8 characters, one uppercase, one digit, and one special character)
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+]).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    // Authenticates user by email and password
    public User login(String email, String password) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()){
            logger.error("Login failed: User with email {} not found.", email);
            throw new NoSuchElementException("User with email " + email + " not found. Please sign up.");

        }
        User user = existingUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Login failed: Incorrect password for email {}", email);
            throw new IllegalArgumentException("Wrong password. Please try again.");
        }
        logger.info("Login successful for email: {}", email);
        return user;
    }

    // Registers a new user, validating email and password
    @Caching(evict = {
            @CacheEvict(value = "totalUsers", key = "'totalUsers'"),
            @CacheEvict(value = "users", allEntries = true)
    })
    public User signUp(User user) {
        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            logger.error("Signup failed: Email {} already in use.", user.getEmail());
            throw new IllegalArgumentException("Email already in use");
        }
        if (!isValidPassword(user.getPassword())) {
            logger.error("Signup failed: Password does not meet complexity requirements for email {}", user.getEmail());
            throw new IllegalArgumentException("Password must be between 8 and 20 characters long, " +
                                                "contain at least one uppercase letter, one digit, and one special character");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getStatus() == null) {
            user.setStatus(Status.ACTIVE);
        }  else {
            // Convert the string status to Status enum
            user.setStatus(Status.valueOf(user.getStatus().name().toUpperCase()));
        }

        user.setRole(Role.USER);
        logger.info("User signed up successfully with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    // Updates the status of a user by ID
    @CacheEvict(value = "users", allEntries = true)
    public  User updateStatus(Long id, String status) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.error("Update failed: User with ID {} not found.", id);
            throw new NoSuchElementException ("User with id " + id + " not found.");
        }
        User user = optionalUser.get();
        try {
            // Convert the string status to Status enum
            Status newStatus = Status.valueOf(status.toUpperCase());
            user.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            logger.error("Update failed: Invalid status value {} for user with ID {}", status, id);
            throw new IllegalArgumentException("Invalid status value. Allowed values are ACTIVE, INACTIVE.");
        }
        logger.info("User status updated successfully for ID: {}", id);
        return userRepository.save(user);
    }

    // Retrieves a list of all users
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        logger.info("Fetching all users from the repository.");
        return userRepository.findAll();
    }

    // Deletes a user by their ID
    @Caching(evict = {
            @CacheEvict(value = "totalUsers", key = "'totalUsers'"),
            @CacheEvict(value = "users", allEntries = true)
    })
    public void deleteUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.error("Deletion failed: User with ID {} not found.", id);
            throw new NoSuchElementException("User with id " + id + " not found.");
        }
        userRepository.deleteById(id);
        logger.info("User with ID {} deleted successfully.", id);
    }

}
