package com.example.usermanagement.config;

import com.example.usermanagement.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//https://dev.to/m1guelsb/authentication-and-authorization-with-spring-boot-4m2n

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    // Defines the security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Disables CSRF protection for stateless APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Configures stateless session management for JWT-based authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Adds a custom security filter before Spring's default authentication filter
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                // Configures route-level access control
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/getAllUsers").hasRole("ADMIN")
                                .requestMatchers("/api/stats").hasRole("ADMIN")
                                .requestMatchers("/api/**").permitAll()
                                .anyRequest().authenticated()
                )
                .build();
    }

    // Provides a password encoder for securely hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Uses BCrypt with strength 12
    }

    // Provides an authentication manager for handling authentication requests
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
