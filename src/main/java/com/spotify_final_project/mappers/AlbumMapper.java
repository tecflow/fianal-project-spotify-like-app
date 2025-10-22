package com.spotify_final_project.mappers;

import com.spotify_final_project.dto.album.AlbumRequest;
import com.spotify_final_project.dto.album.AlbumResponse;
import com.spotify_final_project.model.Album;
import org.aspectj.lang.annotation.After;

public class AlbumMapper {

    public static Album mapToEntity(AlbumRequest request) {
        Album album = new Album();
        album.setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setReleaseDate(request.getReleaseDate())
                .setGenre(request.getGenre());
        return album;
    }

    public static AlbumResponse mapToResponse(Album album) {
        AlbumResponse response = new AlbumResponse();
        response.setId(album.getId());
        response.setTitle(album.getTitle());
        response.setDescription(album.getDescription());
        response.setReleaseDate(album.getReleaseDate());
        response.setGenre(album.getGenre());
        response.setArtistUsername(album.getArtist().getUsername());
        response.setTrackTitles(
                album.getTracks().stream()
                        .map(track -> track.getTitle())
                        .toList()
        );
        return response;
    }
}









