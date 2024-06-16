package com.example.service;

import com.example.domain.Role;
import com.example.domain.User;
import com.example.dto.JwtAuthenticationResponse;
import com.example.dto.SignInRequest;
import com.example.dto.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void signUp_Success() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest("testUser", "test@example.com", "password");
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockedToken");

        // When
        JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);

        // Then
        assertEquals("mockedToken", response.getToken());
        verify(passwordEncoder).encode("password");
        verify(userService).create(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }
}
