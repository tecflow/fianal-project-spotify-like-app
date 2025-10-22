package com.spotify_final_project.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
