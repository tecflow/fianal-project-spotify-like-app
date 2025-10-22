package com.spotify_final_project.exception.album;

public class AlbumNotFoundException extends RuntimeException{
    public AlbumNotFoundException(String message) {
        super(message);
    }

}
