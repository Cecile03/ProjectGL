package com.example.backend.controller;

import com.example.backend.dto.UserInteract;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void signUp_shouldReturnResponseEntity() {
        // Setup
        UserInteract signUpRequest = new UserInteract();
        when(authService.signUp(signUpRequest)).thenReturn(new UserInteract());

        // Execution
        authController.signUp(signUpRequest);

        // Verification
        verify(authService, times(1)).signUp(signUpRequest);
    }

    @Test
    void signUp_shouldReturnBadRequestWhenExceptionThrown() {
        // Setup
        UserInteract signUpRequest = new UserInteract();
        when(authService.signUp(signUpRequest)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<UserInteract> response = authController.signUp(signUpRequest);

        // Verification
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(authService, times(1)).signUp(signUpRequest);
    }

    @Test
    void signIn_shouldReturnResponseEntity() {
        // Setup
        UserInteract signInRequest = new UserInteract();
        when(authService.signIn(signInRequest)).thenReturn(new UserInteract());

        // Execution
        authController.signIn(signInRequest);

        // Verification
        verify(authService, times(1)).signIn(signInRequest);
    }

    @Test
    void refreshToken_shouldReturnResponseEntity() {
        // Setup
        UserInteract refreshTokenRequest = new UserInteract();
        when(authService.refreshToken(refreshTokenRequest)).thenReturn(new UserInteract());

        // Execution
        authController.refreshToken(refreshTokenRequest);

        // Verification
        verify(authService, times(1)).refreshToken(refreshTokenRequest);
    }

    @Test
    void getCurrentUser_shouldReturnUserWhenTokenIsValid() {
        // Setup
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = new User();
        String token = "validToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authService.getUserFromToken(token)).thenReturn(user);

        // Execution
        ResponseEntity<User> response = authController.getCurrentUser(request);

        // Verification
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(authService, times(1)).getUserFromToken(token);
    }

    @Test
    void getCurrentUser_shouldReturnUnauthorizedWhenExceptionThrown() {
        // Setup
        HttpServletRequest request = mock(HttpServletRequest.class);
        String token = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authService.getUserFromToken(token)).thenThrow(new RuntimeException());

        // Execution
        ResponseEntity<User> response = authController.getCurrentUser(request);

        // Verification
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).getUserFromToken(token);
    }

    @Test
    void getCurrentUser_shouldReturnUnauthorizedWhenAuthorizationHeaderDoesNotStartWithBearer() {
        // Setup
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("invalidToken");

        // Execution
        authController.getCurrentUser(request);

        // Verification
        verify(authService, never()).getUserFromToken(anyString());
    }
}
