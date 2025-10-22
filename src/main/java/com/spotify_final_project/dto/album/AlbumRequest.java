package com.spotify_final_project.dto.album;

import com.spotify_final_project.enums.GenreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AlbumRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @NotNull(message = "Genre is required")
    private GenreType genre;
}