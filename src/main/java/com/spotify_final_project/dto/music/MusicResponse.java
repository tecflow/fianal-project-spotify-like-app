package com.spotify_final_project.dto.music;

import com.spotify_final_project.enums.GenreType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicResponse {

    private Long id;
    private String title;
    private GenreType genre;
    private int duration;
    private String artistUsername;
    private String albumName; // Optional, null if no album assigned
}
