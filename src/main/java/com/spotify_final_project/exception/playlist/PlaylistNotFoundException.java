package com.spotify_final_project.exception.playlist;

public class PlaylistNotFoundException extends RuntimeException{
    public PlaylistNotFoundException(String message) {
        super(message);
    }

}
