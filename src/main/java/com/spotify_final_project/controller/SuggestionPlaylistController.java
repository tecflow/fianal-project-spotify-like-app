package com.spotify_final_project.controller;

import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.service.AuthorizationService;
import com.spotify_final_project.service.SuggestionPlaylistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SuggestionPlaylistController {

    private final SuggestionPlaylistService sessionPlaylistService;
    private final AuthorizationService authorizationService;

    @GetMapping("/playlists")
    public Map<GenreType, List<Music>> getSessionPlaylists(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        User user = authorizationService.getLoggedInUser(request);
        return sessionPlaylistService.generateSessionPlaylists(user.getId(), page, pageSize);
    }
}
