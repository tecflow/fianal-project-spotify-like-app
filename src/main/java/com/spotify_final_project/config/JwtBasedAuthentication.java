package com.spotify_final_project.config;

import com.spotify_final_project.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Qualifier("JwtBasedAuthenticationFilter")
public class JwtBasedAuthentication extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        System.out.println("========== JWT FILTER DEBUG ==========");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("HTTP Method: " + request.getMethod());

        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authorizationHeader);

        // Check if header missing or malformed
        if (authorizationHeader == null) {
            System.out.println("⚠️  Missing Authorization header.");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            System.out.println("⚠️  Invalid Authorization format. Must start with 'Bearer '.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();
        System.out.println("Extracted JWT token: " + token);

        try {
            Authentication authentication = userService.authenticate(token);

            // Debug authentication details
            if (authentication != null) {
                System.out.println("✅ Authentication object created: " + authentication.getClass().getSimpleName());
                System.out.println("Username: " + authentication.getName());
                System.out.println("Authorities: " + authentication.getAuthorities());
            } else {
                System.out.println("❌ Authentication returned null from UserService.");
            }

            // Set authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            System.out.println("🚨 Exception while authenticating JWT:");
            e.printStackTrace(System.out);
        }

        filterChain.doFilter(request, response);
        System.out.println("========== END JWT FILTER ==========\n");
    }
}