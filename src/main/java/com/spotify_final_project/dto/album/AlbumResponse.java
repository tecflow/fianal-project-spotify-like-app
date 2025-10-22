package com.spotify_final_project.dto.album;

import com.spotify_final_project.enums.GenreType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AlbumResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private GenreType genre;

    private String artistUsername; // Artist owner
    private List<String> trackTitles; // Titles of music tracks
}