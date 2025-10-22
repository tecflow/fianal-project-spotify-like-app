package com.spotify_final_project.dto.playlist;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreatePlaylistRequest {
    private String name;
    private String description;
}
