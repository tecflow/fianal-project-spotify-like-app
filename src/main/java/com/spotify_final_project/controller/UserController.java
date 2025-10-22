package com.spotify_final_project.controller;

import com.spotify_final_project.dto.Artist.ArtistProfileResponse;
import com.spotify_final_project.dto.auth.UserLoginRequest;
import com.spotify_final_project.dto.register.UserRegisterRequest;
import com.spotify_final_project.dto.response.ApiResponse;
import com.spotify_final_project.dto.response.AuthResponse;
import com.spotify_final_project.dto.user.UserSummary;
import com.spotify_final_project.service.ArtistService;
import com.spotify_final_project.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ArtistService artistService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody @Valid UserRegisterRequest request) {
        String message = userService.register(request);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid UserLoginRequest request) {
        AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping
    public ResponseEntity<List<UserSummary>> getUsers(@RequestParam(defaultValue = "0") int page) {
        List<UserSummary> users = userService.getAllUserNames(PageRequest.of(page, 10, Sort.Direction.ASC, "firstName"));
        return ResponseEntity.ok(users);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyUser(
            @RequestParam String email,
            @RequestParam String code) {

        String message = userService.verifyUser(email, code);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    @GetMapping("/{id}/artist-profile")
    public ResponseEntity<ArtistProfileResponse> getArtistById(@PathVariable Long id) {
        ArtistProfileResponse response = artistService.getArtistProfile(id);
        return ResponseEntity.ok(response);
    }
}