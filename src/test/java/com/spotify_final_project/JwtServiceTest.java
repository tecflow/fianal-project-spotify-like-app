package com.spotify_final_project;

import com.spotify_final_project.config.CustomAuthentication;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import com.spotify_final_project.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Set secret and expiration via reflection
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, "01234567890123456789012345678901"); // 32 bytes for HMAC-SHA

        Field expirationField = JwtService.class.getDeclaredField("expirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 3600000L); // 1 hour
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtService.generateToken("user1", "user1@example.com", Role.LISTENER);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT format: header.payload.signature
    }

    @Test
    void parseToken_ShouldReturnCustomAuthentication_ForValidToken() {
        String token = jwtService.generateToken("user1", "user1@example.com", Role.LISTENER);
        CustomAuthentication auth = (CustomAuthentication) jwtService.parseToken(token);

        assertEquals("user1", auth.getUsername());
        assertEquals("user1@example.com", auth.getEmail());
        assertEquals(Role.LISTENER.name(), auth.getRole());
    }

    @Test
    void parseToken_ShouldThrow_WhenTokenInvalid() {
        String invalidToken = "invalid.token.value";
        assertThrows(InvalidCredentialsException.class, () -> jwtService.parseToken(invalidToken));
    }

    @Test
    void getExpirationTimestamp_ShouldReturnFutureTimestamp() {
        long now = System.currentTimeMillis();
        long expiresAt = jwtService.getExpirationTimestamp();
        assertTrue(expiresAt > now);
    }
}
