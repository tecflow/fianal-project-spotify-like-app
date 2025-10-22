package com.spotify_final_project.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           @Qualifier("JwtBasedAuthenticationFilter")
                                           JwtBasedAuthentication jwtBasedAuthentication) throws Exception {

        http
                // Disable CSRF for REST APIs
                .csrf(AbstractHttpConfigurer::disable)

                // Add JWT filter before the username/password filter
                .addFilterBefore(jwtBasedAuthentication, UsernamePasswordAuthenticationFilter.class)

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // ✅ Public endpoints
                        .requestMatchers(
                                "/users/login",
                                "/users/register",
                                "/users/verify",
                                "/admin/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ✅ Admin-only endpoints
                        .requestMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasRole("ADMIN")



                        // ✅ Music management endpoints
                        .requestMatchers(HttpMethod.POST, "/music/**", "/musics/**").hasAnyRole("ARTIST", "LISTENER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/music/**", "/musics/**").hasAnyRole("ARTIST", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/music/**", "/musics/**").hasAnyRole("ARTIST", "ADMIN")

                        // ✅ All other requests require authentication
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}