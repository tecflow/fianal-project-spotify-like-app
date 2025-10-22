package com.spotify_final_project.dto.playlist;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class PlaylistResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerUsername;
    private List<String> trackTitles;
}
