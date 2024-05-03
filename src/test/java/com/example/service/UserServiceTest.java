package com.example.service;

import com.example.domain.Role;
import com.example.domain.User;
import com.example.repository.ImageRepository;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("User Service Test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;

    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private ImageRepository repository;
    @Mock
    private MinioService minioService;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Test create method - user does not exist")
    void createUserNotExists() {
        // Given
        User newUser = new User();
        newUser.setUsername("testUser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("password");
        newUser.setRole(Role.ROLE_USER);

        when(userRepository.existsByUsername(newUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(newUser);

        // When
        User createdUser = userService.create(newUser);

        // Then
        assertNotNull(createdUser);
        assertEquals(newUser.getUsername(), createdUser.getUsername());
        assertEquals(newUser.getEmail(), createdUser.getEmail());
        assertEquals(newUser.getRole(), createdUser.getRole());
        verify(userRepository, times(1)).existsByUsername(newUser.getUsername());
        verify(userRepository, times(1)).existsByEmail(newUser.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test create method - user already exists")
    void createUserExists() {
        // Given
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("password");
        existingUser.setRole(Role.ROLE_USER);

        when(userRepository.existsByUsername(existingUser.getUsername())).thenReturn(true);

        // When / Then
        assertThrows(RuntimeException.class, () -> userService.create(existingUser));
        verify(userRepository, times(1)).existsByUsername(existingUser.getUsername());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test getByUsername method - user exists")
    void getByUsernameExists() {
        // Given
        String username = "testUser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        User retrievedUser = userService.getByUsername(username);

        // Then
        assertNotNull(retrievedUser);
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getUsername(), retrievedUser.getUsername());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
        assertEquals(user.getRole(), retrievedUser.getRole());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Test getByUsername method - user does not exist")
    void getByUsernameNotExists() {
        // Given
        String username = "nonExistingUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(Exception.class, () -> userService.getByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Test getCurrentUser method - user authenticated")
    void getCurrentUserAuthenticated() {
        // Given
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testUser");
        currentUser.setEmail("test@example.com");
        currentUser.setPassword("password");
        currentUser.setRole(Role.ROLE_USER);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(currentUser);

        // When
        User retrievedUser = userService.getCurrentUser();

        // Then
        assertNotNull(retrievedUser);
        assertEquals(currentUser.getId(), retrievedUser.getId());
        assertEquals(currentUser.getUsername(), retrievedUser.getUsername());
        assertEquals(currentUser.getEmail(), retrievedUser.getEmail());
        assertEquals(currentUser.getRole(), retrievedUser.getRole());
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    @DisplayName("Test getCurrentUser method - user not authenticated")
    void getCurrentUserNotAuthenticated() {
        // Given
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        User retrievedUser = userService.getCurrentUser();

        // Then
        assertNull(retrievedUser);
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    void create_UserWithSameUsernameExists_ThrowsException() {
        // Given
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        when(userRepository.existsByUsername(existingUser.getUsername())).thenReturn(true);

        // When/Then
        assertThrows(RuntimeException.class, () -> userService.create(existingUser));
    }

    @Test
    void create_UserWithSameEmailExists_ThrowsException() {
        // Given
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        when(userRepository.existsByEmail(existingUser.getEmail())).thenReturn(true);

        // When/Then
        assertThrows(RuntimeException.class, () -> userService.create(existingUser));
    }

}
