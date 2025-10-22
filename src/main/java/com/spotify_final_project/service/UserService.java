package com.spotify_final_project.service;

import com.spotify_final_project.config.CustomAuthentication;
import com.spotify_final_project.dto.auth.UserLoginRequest;
import com.spotify_final_project.dto.register.UserRegisterRequest;
import com.spotify_final_project.dto.response.AuthResponse;
import com.spotify_final_project.dto.user.UserSummary;
import com.spotify_final_project.enums.AccountStatus;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import com.spotify_final_project.mappers.UserMapper;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    public String register(UserRegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new InvalidCredentialsException("Username already taken");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidCredentialsException("Email already in use");
        }

        User user = UserMapper.mapToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            Role selectedRole = Role.valueOf(request.getRole().toUpperCase());
            if (selectedRole == Role.ADMIN) {
                throw new InvalidCredentialsException("Cannot register as ADMIN");
            }
            user.setRole(selectedRole); // <-- set enum directly
        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialsException("Invalid role. Choose ARTIST or LISTENER.");
        }

        user.setVerified(false);
        user.setStatus(AccountStatus.PENDING);

        String verificationCode = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setVerificationCode(verificationCode);

        userRepository.save(user);

        mailService.sendVerificationEmail(user.getEmail(), verificationCode);

        return "User registered successfully! Please check your email to verify your account.";
    }


    public AuthResponse login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (user.getStatus() == AccountStatus.BLOCKED) {
            throw new InvalidCredentialsException("Your account has been blocked. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (!user.isVerified()) {
            throw new InvalidCredentialsException("Please verify your email before logging in");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getRole());
        long expiresAt = jwtService.getExpirationTimestamp();

        return new AuthResponse(token, expiresAt);
    }

    public Authentication authenticate(String token) {
        System.out.println("Parsing JWT token: " + token);

        CustomAuthentication auth = (CustomAuthentication) jwtService.parseToken(token);

        System.out.println("Authentication created: " + auth.getAuthorities());

        userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        return auth;
    }

    public List<UserSummary> getAllUserNames(Pageable pageable) {
        return userRepository.findAllProjectedBy(pageable);
    }

    public String verifyUser(String email, String code) {
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (user.isVerified()) {
            return "User is already verified!";
        }

        if (user.getVerificationCode().equals(code)) {
            user.setVerified(true);
            user.setVerificationCode(null);
            user.setStatus(AccountStatus.ACTIVE);
            userRepository.save(user);
            return "Account verified successfully!";
        } else {
            throw new InvalidCredentialsException("Invalid verification code");
        }
    }
}
