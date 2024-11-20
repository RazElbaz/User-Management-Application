package com.example.usermanagement.config;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.enums.Status;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
//https://mkyong.com/spring-boot/spring-boot-commandlinerunner-example/
@Configuration
public class AdminUserConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheManager cacheManager; // CacheManager for managing Redis cache

    // Load admin email and password from properties
    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    // CommandLineRunner to initialize the admin user during application startup
    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            // Check if an admin user already exists
            Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);
            if (existingAdmin.isEmpty()) {
                // Create a new admin user if not already present
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword)); // Encrypt the password
                admin.setStatus(Status.ACTIVE);
                admin.setRole(Role.ADMIN);

                userRepository.save(admin); // Save the admin user in the database
                System.out.println("Admin user created successfully.");

                // Clear the "users" cache to ensure consistency
                if (cacheManager.getCache("users") != null) {
                    cacheManager.getCache("users").clear();
                }
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}
