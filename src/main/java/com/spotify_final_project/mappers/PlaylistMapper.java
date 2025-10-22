package com.spotify_final_project.mappers;


import com.spotify_final_project.dto.playlist.CreatePlaylistRequest;
import com.spotify_final_project.dto.playlist.PlaylistResponse;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.Playlist;

import java.util.stream.Collectors;

public class PlaylistMapper {

    public static Playlist mapToEntity(CreatePlaylistRequest request) {
        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        return playlist;
    }

    public static PlaylistResponse mapToResponse(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .ownerUsername(playlist.getOwner().getUsername())
                .trackTitles(playlist.getTracks().stream()
                        .map(Music::getTitle)
                        .collect(Collectors.toList()))
                .build();
    }
}
