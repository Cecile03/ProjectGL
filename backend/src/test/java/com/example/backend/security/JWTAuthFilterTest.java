package com.example.backend.security;

import com.example.backend.model.User;
import com.example.backend.service.JWTUtils;
import com.example.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JWTAuthFilterTest {

    @InjectMocks
    private JWTAuthFilter jwtAuthFilter;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        String token = "Bearer valid.jwt.token";
        String email = "user@example.com";
        User user = mock(User.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.extractEmail(anyString())).thenReturn(email);
        when(userService.loadUserByEmail(anyString())).thenReturn(user);
        when(jwtUtils.isTokenValid(anyString(), (User) any(UserDetails.class))).thenReturn(true);
        when(user.getAuthorities()).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader("Authorization");
        verify(jwtUtils, times(1)).extractEmail(anyString());
        verify(userService, times(1)).loadUserByEmail(anyString());
        verify(jwtUtils, times(1)).isTokenValid(anyString(), (User) any(UserDetails.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String token = "Bearer invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.extractEmail(anyString())).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader("Authorization");
        verify(jwtUtils, times(1)).extractEmail(anyString());
        verify(userService, never()).loadUserByEmail(anyString());
        verify(jwtUtils, never()).isTokenValid(anyString(), (User) any(UserDetails.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader("Authorization");
        verify(jwtUtils, never()).extractEmail(anyString());
        verify(userService, never()).loadUserByEmail(anyString());
        verify(jwtUtils, never()).isTokenValid(anyString(), (User) any(UserDetails.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
