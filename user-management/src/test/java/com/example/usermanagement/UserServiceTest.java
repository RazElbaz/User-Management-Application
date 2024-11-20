package com.example.usermanagement;

import com.example.usermanagement.config.AdminUserConfig;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.enums.Role;
import com.example.usermanagement.enums.Status;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.cache.Cache;
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;

    @InjectMocks
    private AdminUserConfig adminUserConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testSignUp_Successful() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("Valid@123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.signUp(user);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void testUpdateStatus_Successful() {
        Long userId = 1L;
        String newStatus = "INACTIVE";
        User user = new User();
        user.setId(userId);
        user.setStatus(Status.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateStatus(userId, newStatus);

        assertNotNull(result);
        assertEquals(Status.INACTIVE, result.getStatus());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void testDeleteUserById_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void testLogin_Success() {
        // Given
        String email = "test@example.com";
        String password = "Test@123";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        // When
        User result = userService.login(email, password);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        String email = "unknown@example.com";
        String password = "Test@123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Then
        assertThrows(NoSuchElementException.class, () -> userService.login(email, password));
    }

    @Test
    void testLogin_InvalidPassword() {
        // Given
        String email = "test@example.com";
        String password = "wrongPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.login(email, password));
    }

    @Test
    void createAdminUser_whenAdminExists_shouldNotCreateAdminUser() {
        // Arrange
        String adminEmail = "admin@example.com";
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(new User()));

        // Act
        adminUserConfig.createAdminUser();

        // Assert
        verify(userRepository, never()).save(any());
        verify(cacheManager, never()).getCache("users");
    }
    @Test
    void testSignUp_Success() {
        // Arrange
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("Valid@1234");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.signUp(user);

        // Assert
        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        verify(passwordEncoder).encode("Valid@1234");
    }

    @Test
    void testSignUp_EmailAlreadyExists() {
        // Arrange
        User user = new User();
        user.setEmail("existing@example.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user));
    }

    @Test
    void testSignUp_InvalidPasswordFormat() {
        // Arrange
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("weakpass");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user));
    }
    @Test
    void testLogin_EmailFormatInvalid() {
        // Given
        String email = "invalid-email";
        String password = "Test@123";

        // Assert
        assertThrows(NoSuchElementException.class, () -> userService.login(email, password));
    }


    @Test
    void testSignUp_PasswordTooShort() {
        // Arrange
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("Short1!");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user));
    }

    @Test
    void testSignUp_PasswordWithoutUppercase() {
        // Arrange
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("lowercase1!");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user));
    }

    @Test
    void testSignUp_PasswordWithoutSpecialCharacter() {
        // Arrange
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("Uppercase1");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user));
    }

    @Test
    void testSignUp_PasswordWithoutDigit() {
        // Arrange
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("Uppercase@");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(user));
    }


    @Test
    void testUpdateStatus_InvalidEnum() {
        // Given
        Long userId = 1L;
        String status = "UNKNOWN"; // Invalid status value
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateStatus(userId, status));
    }

    @Test
    void testGetAllUsers_EmptyDatabase() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<User> users = userService.getAllUsers();

        // Then
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testDeleteUser_UserAlreadyDeleted() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> userService.deleteUserById(userId));
    }

    @Test
    void testDeleteUser_DatabaseConnectivityIssue() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenThrow(new DataAccessException("Database error") {});

        // Assert
        assertThrows(DataAccessException.class, () -> userService.deleteUserById(userId));
    }


    @Test
    void testUpdateStatus_Success() {
        // Arrange
        Long userId = 1L;
        String newStatus = "INACTIVE";

        // Create a user with ACTIVE status
        User user = new User();
        user.setId(userId);
        user.setStatus(Status.ACTIVE);

        // Mock the repository to return the user when searched by ID
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock the save method to return the updated user
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.updateStatus(userId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(Status.INACTIVE, result.getStatus());

        // Verify the repository's save method was called with the updated user
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUserById(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> userService.deleteUserById(userId));
    }

}
