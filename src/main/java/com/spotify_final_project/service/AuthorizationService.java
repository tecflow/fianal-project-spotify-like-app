package com.spotify_final_project.service;

import com.spotify_final_project.config.CustomAuthentication;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthorizationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public User getLoggedInUser(HttpServletRequest request) { // ვიღებთ JWT-ს Authorization-ის ჰედერიდან, ვაქცევთ მას CustomAuthentication-ის ობიექტად და ამ იფორმაციით ვიღებთ იუზერს ბაზიდან
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidCredentialsException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer "
        Authentication auth = jwtService.parseToken(token);
        CustomAuthentication customAuth = (CustomAuthentication) auth;

        return userRepository.findByUsername(customAuth.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
    }
}