package com.spotify_final_project.controller;

import com.spotify_final_project.model.User;
import com.spotify_final_project.service.AuthorizationService;
import com.spotify_final_project.service.MusicAddService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/music")
@RequiredArgsConstructor
@RestController
public class MusicAddController {

    private final MusicAddService musicService;
    private final AuthorizationService authorizationService;

    @PostMapping("/playlist/{playlistId}/add/{musicId}")
    public ResponseEntity<String> addToPlaylist(@PathVariable Long playlistId,
                                                @PathVariable Long musicId,
                                                HttpServletRequest request) {
        User user = authorizationService.getLoggedInUser(request);
        musicService.addMusicToPlaylist(playlistId, musicId, user.getId());
        return ResponseEntity.ok("Music added to playlist successfully");
    }
}