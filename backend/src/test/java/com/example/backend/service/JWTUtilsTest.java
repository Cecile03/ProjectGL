package com.example.backend.service;

import com.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.Mac;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JWTUtilsTest {

    private JWTUtils jwtUtils;

    @Mock
    private User user;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtils = new JWTUtils();
    }

    @Test
    void shouldGenerateToken() {
        when(user.getEmail()).thenReturn("test@example.com");
        String token = jwtUtils.generateToken(user);
        assertNotNull(token);
    }

    @Test
    void shouldGenerateRefreshToken() {
        Map<String, Object> claims = new HashMap<>();
        when(userDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtUtils.generateRefreshToken(claims, userDetails);
        assertNotNull(token);
    }

    @Test
    void shouldExtractEmail() {
        when(user.getEmail()).thenReturn("test@example.com");
        String token = jwtUtils.generateToken(user);
        String email = jwtUtils.extractEmail(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void shouldReturnNullWhenExtractingEmailFromInvalidToken() {
        String email = jwtUtils.extractEmail("invalidToken");
        assertNull(email);
    }

    @Test
    void shouldValidateToken() {
        when(user.getEmail()).thenReturn("test@example.com");
        String token = jwtUtils.generateToken(user);
        assertTrue(jwtUtils.isTokenValid(token, user));
    }

    @Test
    void shouldInvalidateTokenWhenEmailDoesNotMatch() {
        when(user.getEmail()).thenReturn("test@example.com");
        String token = jwtUtils.generateToken(user);
        when(user.getEmail()).thenReturn("different@example.com");
        assertFalse(jwtUtils.isTokenValid(token, user));
    }

    @Test
    void shouldInvalidateExpiredToken() {
        when(user.getEmail()).thenReturn("test@example.com");
        String token = jwtUtils.generateToken(user);
        assertFalse(jwtUtils.isTokenExpired(token));
    }

    @Test
    void shouldCorrectlyUseKey() throws NoSuchAlgorithmException, InvalidKeyException {
        when(user.getEmail()).thenReturn("test@example.com");
        String token = jwtUtils.generateToken(user);

        // Split the token into parts
        String[] parts = token.split("\\.");

        // The signature is the third part of the token
        String signature = parts[2];

        // Generate the expected signature
        String header = parts[0];
        String payload = parts[1];
        String data = header + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(jwtUtils.getKey());
        byte[] expectedSignatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        String expectedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(expectedSignatureBytes);

        // Check if the signature in the token matches the expected signature
        assertEquals(expectedSignature, signature);
    }
}
