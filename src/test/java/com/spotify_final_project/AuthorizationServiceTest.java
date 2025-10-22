package com.spotify_final_project;

import com.spotify_final_project.config.CustomAuthentication;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.AuthorizationService;
import com.spotify_final_project.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private AuthorizationService authorizationService;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userRepository = mock(UserRepository.class);
        request = mock(HttpServletRequest.class);
        authorizationService = new AuthorizationService(jwtService, userRepository);
    }

    @Test
    void getLoggedInUser_ShouldReturnUser_WhenTokenValid() {
        String token = "valid-token";
        User user = new User();
        user.setUsername("user1");

        CustomAuthentication customAuth = mock(CustomAuthentication.class);
        when(customAuth.getUsername()).thenReturn("user1");
        when(jwtService.parseToken(token)).thenReturn(customAuth);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        User result = authorizationService.getLoggedInUser(request);

        assertEquals(user, result);
    }

    @Test
    void getLoggedInUser_ShouldThrow_WhenHeaderMissing() {
        when(request.getHeader("Authorization")).thenReturn(null);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authorizationService.getLoggedInUser(request)
        );

        assertEquals("Missing or invalid Authorization header", exception.getMessage());
    }

    @Test
    void getLoggedInUser_ShouldThrow_WhenHeaderInvalid() {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authorizationService.getLoggedInUser(request)
        );

        assertEquals("Missing or invalid Authorization header", exception.getMessage());
    }

    @Test
    void getLoggedInUser_ShouldThrow_WhenUserNotFound() {
        String token = "token";
        CustomAuthentication customAuth = mock(CustomAuthentication.class);
        when(customAuth.getUsername()).thenReturn("ghost");
        when(jwtService.parseToken(token)).thenReturn(customAuth);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authorizationService.getLoggedInUser(request)
        );

        assertEquals("User not found", exception.getMessage());
    }
}
