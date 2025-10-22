package com.spotify_final_project.exception.auth;


public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
