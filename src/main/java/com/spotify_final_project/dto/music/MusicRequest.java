package com.spotify_final_project.dto.music;

import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Album;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicRequest {

    @NotNull(message = "Album must be provided")
    private Long albumId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Genre must be provided")
    private GenreType genre;

    @Min(value = 1, message = "Duration must be at least 1 second")
    private int duration;
}
