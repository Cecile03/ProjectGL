package com.example.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private JWTAuthFilter jwtAuthFilter;

    private SecurityConfig securityConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig(userDetailsService, jwtAuthFilter);
    }

    @Test
    void testRoleHierarchyBean() {
        RoleHierarchy roleHierarchy = securityConfig.roleHierarchy();
        assertNotNull(roleHierarchy);
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder);
    }

    @Test
    void testAuthenticationManagerBean() throws Exception {
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager authManager = securityConfig.authenticationManager(authenticationConfiguration);
        assertNotNull(authManager);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void authenticationProviderShouldReturnConfiguredDaoAuthenticationProvider() {
        AuthenticationProvider result = securityConfig.authenticationProvider();
        assertTrue(result instanceof DaoAuthenticationProvider);
    }

}
