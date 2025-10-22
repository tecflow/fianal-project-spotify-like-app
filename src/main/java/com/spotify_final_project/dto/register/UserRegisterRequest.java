package com.spotify_final_project.dto.register;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRegisterRequest {
    @NotNull(message = "First name is required")
    @Size(min = 2, max = 32, message = "Size of first name should be between 2 and 32")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 2, max = 64, message = "Size of last name should be between 2 and 64")
    private String lastName;

    @NotNull(message = "Username is required")
    @Size(min = 4, max = 16, message = "Size of username should be between 4 and 16")
    private String username;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birthdate must be in the past.")
    private LocalDate birthDate;

    @NotBlank(message = "Email is required")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Size of password should be between 6 and 20")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ARTIST|LISTENER", message = "Role must be ARTIST or LISTENER")
    private String role;
}
