package com.spotify_final_project.mappers;

import com.spotify_final_project.dto.music.MusicRequest;
import com.spotify_final_project.dto.music.MusicResponse;
import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.Music;

public class MusicMapper {

    public static Music mapToEntity(MusicRequest musicRequest) {
        Music music = new Music();
        music.setTitle(musicRequest.getTitle());
        music.setGenre(musicRequest.getGenre());
        music.setDuration(musicRequest.getDuration());
        return music;
    }

    public static MusicResponse mapToResponse(Music music) {
        MusicResponse response = new MusicResponse();
        response.setId(music.getId());
        response.setTitle(music.getTitle());
        response.setGenre(music.getGenre());
        response.setDuration(music.getDuration());
        response.setArtistUsername(music.getArtist().getUsername());
        response.setAlbumName(music.getAlbum() != null ? music.getAlbum().getTitle() : null);
        return response;
    }
}
