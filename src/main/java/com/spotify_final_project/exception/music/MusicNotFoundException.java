package com.spotify_final_project.exception.music;

public class MusicNotFoundException extends RuntimeException{
    public MusicNotFoundException(String message) {
        super(message);
    }
}
