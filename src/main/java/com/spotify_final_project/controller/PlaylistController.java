package com.spotify_final_project.controller;

import com.spotify_final_project.dto.playlist.CreatePlaylistRequest;
import com.spotify_final_project.dto.playlist.PlaylistResponse;
import com.spotify_final_project.model.User;
import com.spotify_final_project.service.AuthorizationService;
import com.spotify_final_project.service.PlaylistService;
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
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(
            summary = "Create playlist (listener or artist)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody @Valid CreatePlaylistRequest request,
                                                           HttpServletRequest httpRequest) {
        User user = authorizationService.getLoggedInUser(httpRequest);
        PlaylistResponse response = playlistService.createPlaylist(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update playlist (owner or admin)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<PlaylistResponse> updatePlaylist(@PathVariable Long id,
                                                           @RequestBody @Valid CreatePlaylistRequest request,
                                                           HttpServletRequest httpRequest) {
        User user = authorizationService.getLoggedInUser(httpRequest);
        PlaylistResponse response = playlistService.updatePlaylist(id, request, user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete playlist (owner or admin)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<String> deletePlaylist(@PathVariable Long id,
                                                 HttpServletRequest httpRequest) {
        User user = authorizationService.getLoggedInUser(httpRequest);
        playlistService.deletePlaylist(id, user.getId());
        return ResponseEntity.ok("Playlist deleted successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get playlist by ID")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable Long id) {
        PlaylistResponse response = playlistService.getPlaylistById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all playlists")
    public ResponseEntity<List<PlaylistResponse>> getAllPlaylists() {
        List<PlaylistResponse> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(playlists);
    }
}
