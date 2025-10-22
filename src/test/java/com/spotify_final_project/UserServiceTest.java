package com.spotify_final_project;


import com.spotify_final_project.dto.auth.UserLoginRequest;
import com.spotify_final_project.dto.register.UserRegisterRequest;
import com.spotify_final_project.dto.response.AuthResponse;
import com.spotify_final_project.enums.AccountStatus;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.JwtService;
import com.spotify_final_project.service.MailService;
import com.spotify_final_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private MailService mailService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        mailService = mock(MailService.class);

        userService = new UserService(userRepository, passwordEncoder, jwtService, mailService);
    }

    // ----------------- REGISTER TESTS -----------------
    @Test
    void register_ShouldRegisterUserSuccessfully() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@example.com");
        request.setPassword("1234");
        request.setRole("LISTENER");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        String result = userService.register(request);

        assertTrue(result.contains("User registered successfully"));
        verify(mailService, times(1)).sendVerificationEmail(eq("user1@example.com"), anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenUsernameTaken() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new User()));

        assertThrows(InvalidCredentialsException.class, () -> userService.register(request));
    }

    @Test
    void register_ShouldThrow_WhenEmailTaken() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user1");
        request.setEmail("taken@example.com");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(InvalidCredentialsException.class, () -> userService.register(request));
    }

    @Test
    void register_ShouldThrow_WhenAdminRoleSelected() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@example.com");
        request.setPassword("pass");
        request.setRole("ADMIN");

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.register(request));
    }

    @Test
    void register_ShouldThrow_WhenInvalidRole() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@example.com");
        request.setPassword("pass");
        request.setRole("INVALID_ROLE");

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.register(request));
    }

    // ----------------- LOGIN TESTS -----------------
    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("user1");
        request.setPassword("pass");

        User user = new User();
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPassword("encodedPass");
        user.setVerified(true);
        user.setRole(Role.LISTENER);
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
        when(jwtService.generateToken(any(), any(), any())).thenReturn("jwt-token");
        when(jwtService.getExpirationTimestamp()).thenReturn(999999L);

        AuthResponse response = userService.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals(999999L, response.expiresAt());
    }

    @Test
    void login_ShouldThrow_WhenUserBlocked() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("user1");
        request.setPassword("pass");

        User user = new User();
        user.setStatus(AccountStatus.BLOCKED);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void login_ShouldThrow_WhenPasswordInvalid() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("user1");
        request.setPassword("wrong");

        User user = new User();
        user.setUsername("user1");
        user.setPassword("encoded");
        user.setVerified(true);
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void login_ShouldThrow_WhenNotVerified() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("user1");
        request.setPassword("pass");

        User user = new User();
        user.setPassword("encoded");
        user.setVerified(false);
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        UserLoginRequest req = new UserLoginRequest();
        req.setUsername("ghost");
        req.setPassword("123");
        assertThrows(InvalidCredentialsException.class, () -> userService.login(req));
    }

    // ----------------- VERIFY USER TESTS -----------------
    @Test
    void verifyUser_ShouldActivateAccount_WhenCodeMatches() {
        User user = new User();
        user.setVerificationCode("123456");
        user.setVerified(false);
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        String result = userService.verifyUser("user@example.com", "123456");

        assertTrue(result.contains("Account verified successfully"));
        assertEquals(AccountStatus.ACTIVE, user.getStatus());
        assertTrue(user.isVerified());
        verify(userRepository).save(user);
    }

    @Test
    void verifyUser_ShouldThrow_WhenCodeInvalid() {
        User user = new User();
        user.setVerificationCode("999999");
        user.setVerified(false);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> userService.verifyUser("user@example.com", "000000"));
    }

    @Test
    void verifyUser_ShouldReturnMessage_WhenAlreadyVerified() {
        User user = new User();
        user.setVerified(true);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        String result = userService.verifyUser("user@example.com", "123");
        assertTrue(result.contains("already verified"));
    }

    @Test
    void verifyUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class,
                () -> userService.verifyUser("ghost@example.com", "123"));
    }
}