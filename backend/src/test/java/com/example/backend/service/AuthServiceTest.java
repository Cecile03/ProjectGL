package com.example.backend.service;

import com.example.backend.dto.UserInteract;
import com.example.backend.model.User;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        UserInteract registrationRequest = new UserInteract();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setId(1);
        user.setGradePast(0.0);
        when(userService.saveUser(any(User.class))).thenReturn(user);

        UserInteract result = authService.signUp(registrationRequest);
        assertEquals(200, result.getStatusCode());
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void signUp_ShouldThrowException_WhenUserServiceFails() {
        // Given
        UserInteract registrationRequest = new UserInteract();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setRoles(List.of("SS"));

        // Simulate a service failure
        when(userService.saveUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // When
        UserInteract result = authService.signUp(registrationRequest);

        // Then
        assertEquals(500, result.getStatusCode());
        assertEquals("Database error", result.getError());
    }

    @Test
    void testSignIn() {
        UserInteract signinRequest = new UserInteract();
        signinRequest.setEmail("test@example.com");
        signinRequest.setPassword("password");
        User user = new User();
        user.setEmail(signinRequest.getEmail());
        when(userService.loadUserByEmail(signinRequest.getEmail())).thenReturn(user);
        when(jwtUtils.generateToken(user)).thenReturn("token");
        when(jwtUtils.generateRefreshToken(new HashMap<>(), user)).thenReturn("refreshToken");

        UserInteract result = authService.signIn(signinRequest);
        assertEquals(200, result.getStatusCode());
        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),signinRequest.getPassword()));
    }

    @Test
    void signIn_ShouldThrowException_WhenAuthenticationFails() {
        // Given
        UserInteract signinRequest = new UserInteract();
        signinRequest.setEmail("test@example.com");
        signinRequest.setPassword("password");

        // Simulate an authentication failure
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword())))
                .thenThrow(new RuntimeException("Authentication error"));

        // When
        UserInteract result = authService.signIn(signinRequest);

        // Then
        assertEquals(500, result.getStatusCode());
        assertEquals("Authentication error", result.getError());
    }

    @Test
    void testRefreshToken() {
        UserInteract refreshTokenRequest = new UserInteract();
        refreshTokenRequest.setToken("token");
        User user = new User();
        user.setEmail("test@example.com");
        when(jwtUtils.extractEmail(refreshTokenRequest.getToken())).thenReturn(user.getEmail());
        when(userService.loadUserByEmail(user.getEmail())).thenReturn(user);
        when(jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("newToken");

        UserInteract result = authService.refreshToken(refreshTokenRequest);
        assertEquals(200, result.getStatusCode());
        verify(jwtUtils, times(1)).isTokenValid(refreshTokenRequest.getToken(), user);
    }

    @Test
    void refreshToken_ShouldReturnUnauthorized_WhenTokenIsInvalid() {
        // Given
        UserInteract refreshTokenRequest = new UserInteract();
        refreshTokenRequest.setToken("invalidToken");
        User user = new User();
        user.setEmail("test@example.com");
        when(jwtUtils.extractEmail(refreshTokenRequest.getToken())).thenReturn(user.getEmail());
        when(userService.loadUserByEmail(user.getEmail())).thenReturn(user);
        when(jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)).thenReturn(false);

        // When
        UserInteract result = authService.refreshToken(refreshTokenRequest);

        // Then
        assertEquals(401, result.getStatusCode());
        assertEquals("Invalid or Expired Token", result.getMessage());
    }

    @Test
    void testSetDefaultPassword() {
        User user = new User();
        authService.setDefaultPassword(user);
        verify(passwordEncoder, times(1)).encode(null);
    }

    @Test
    void testGetUserFromToken_InvalidToken() {
        String token = "invalidToken";
        when(jwtUtils.getKey()).thenReturn(Keys.secretKeyFor(SignatureAlgorithm.HS256));
        assertThrows(RuntimeException.class, () -> authService.getUserFromToken(token));
    }

    @Test
    void testGetUserFromToken_NullToken() {
        assertThrows(IllegalArgumentException.class, () -> authService.getUserFromToken(null));
    }
}