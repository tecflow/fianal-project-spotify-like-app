package com.spotify_final_project.dto.Artist;

import com.spotify_final_project.enums.GenreType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder
public class ArtistProfileResponse {
    private Long artistId;
    private String firstName;
    private String lastName;
    private String username;
    private Set<GenreType> genres;
    private List<String> similarArtists; // usernames of similar artists
    private String message = "Similar artists generated based on genres.";
}
