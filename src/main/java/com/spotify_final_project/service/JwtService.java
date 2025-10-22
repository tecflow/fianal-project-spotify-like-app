package com.spotify_final_project.service;

import com.spotify_final_project.config.CustomAuthentication;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String secret;

    @Value("${app.security.jwt.expiration}")
    private long expirationMs;


    public String generateToken(String username, String email, Role role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .claim("username", username)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }


    public long getExpirationTimestamp() {
        return Instant.now().plus(expirationMs, ChronoUnit.MILLIS).toEpochMilli();
    }

    public Authentication parseToken(String token) {  //JWT-ის ვასუფთავებთ, ვიღებთ მისით payload-s და მასში არსებულ ინფორმაციას ვიყენებთ რომ შევქმნათ CustomAuthentication-ის ობიექტი
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();
            System.out.println("Parsing JWT token: " + token);

            // <-- Add this line to debug the JWT payload
            System.out.println("JWT Claims: " + claims);

            return new CustomAuthentication(
                    claims.get("role", String.class),
                    claims.get("email", String.class),
                    claims.get("username", String.class)
            );
        } catch (JwtException e) {
            throw new InvalidCredentialsException("Invalid or expired token");
        }
    }
}
