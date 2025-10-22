package com.spotify_final_project.controller;

import com.spotify_final_project.dto.music.MusicRequest;
import com.spotify_final_project.dto.music.MusicResponse;
import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.service.AuthorizationService;
import com.spotify_final_project.service.ListenService;
import com.spotify_final_project.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/musics")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;
    private final AuthorizationService authorizationService;
    private final ListenService listenService;

    @PostMapping
    @Operation(
            summary = "Add music (listener or artist)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<MusicResponse> addMusic(@RequestBody @Valid MusicRequest request,
                                                  HttpServletRequest httpRequest) {
        User user = authorizationService.getLoggedInUser(httpRequest);
        MusicResponse response = musicService.createMusic(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update music (owner or admin)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<MusicResponse> updateMusic(@PathVariable Long id,
                                                     @RequestBody @Valid MusicRequest request,
                                                     HttpServletRequest httpRequest) {
        User user = authorizationService.getLoggedInUser(httpRequest);
        MusicResponse response = musicService.updateMusic(id, request, user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete music (owner or admin)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<String> deleteMusic(@PathVariable Long id,
                                              HttpServletRequest httpRequest) {
        User user = authorizationService.getLoggedInUser(httpRequest);
        musicService.deleteMusic(id, user.getId());
        return ResponseEntity.ok("Music deleted successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get music by ID")
    public ResponseEntity<MusicResponse> getMusicById(@PathVariable Long id, HttpServletRequest request) {
        MusicResponse response = musicService.getMusicById(id);
        User user = authorizationService.getLoggedInUser(request);
        Music music = musicService.getMusicEntityById(id);
        listenService.incrementListenCount(user, music);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all musics")
    public ResponseEntity<List<MusicResponse>> getAllMusics() {
        List<MusicResponse> musics = musicService.getAllMusics();
        return ResponseEntity.ok(musics);
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Music>> searchByTitle(@RequestParam("q") String title) {
        List<Music> results = musicService.searchByTitle(title);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/artist")
    public ResponseEntity<List<Music>> searchByArtist(@RequestParam("q") String artistUsername) {
        List<Music> results = musicService.searchByArtist(artistUsername);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Music>> searchByTitleOrArtist(@RequestParam("q") String keyword) {
        List<Music> results = musicService.searchByTitleOrArtist(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/genre/{genre}")
    @Operation(summary = "Get all music by genre")
    public ResponseEntity<List<Music>> getMusicByGenre(@PathVariable GenreType genre) {
        List<Music> musics = musicService.getMusicByGenre(genre);
        return ResponseEntity.ok(musics);
    }

    @GetMapping("/artist/{artistId}")
    @Operation(summary = "Get all music by a specific artist")
    public ResponseEntity<List<Music>> getMusicByArtist(@PathVariable Long artistId) {
        List<Music> musics = musicService.getMusicByArtist(artistId);
        return ResponseEntity.ok(musics);
    }
}