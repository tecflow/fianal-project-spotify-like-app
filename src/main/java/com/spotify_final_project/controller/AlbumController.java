package com.spotify_final_project.controller;

import com.spotify_final_project.dto.album.AlbumRequest;
import com.spotify_final_project.dto.album.AlbumResponse;
import com.spotify_final_project.model.User;
import com.spotify_final_project.service.AlbumService;
import com.spotify_final_project.service.AuthorizationService;
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
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @Operation(
            summary = "Create album (artist only)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Bearer token")
            }
    )
    public ResponseEntity<AlbumResponse> createAlbum(@RequestBody @Valid AlbumRequest request,
                                                     HttpServletRequest httpRequest) {
        User artist = authorizationService.getLoggedInUser(httpRequest);
        AlbumResponse response = albumService.createAlbum(request, artist.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update album (artist owner or admin)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<AlbumResponse> updateAlbum(@PathVariable Long id,
                                                     @RequestBody @Valid AlbumRequest request,
                                                     HttpServletRequest httpRequest) {
        User requester = authorizationService.getLoggedInUser(httpRequest);
        AlbumResponse response = albumService.updateAlbum(id, request, requester.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete album (artist owner or admin)",
            parameters = {
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true)
            }
    )
    public ResponseEntity<String> deleteAlbum(@PathVariable Long id,
                                              HttpServletRequest httpRequest) {
        User requester = authorizationService.getLoggedInUser(httpRequest);
        albumService.deleteAlbum(id, requester.getId());
        return ResponseEntity.ok("Album deleted successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get album by ID")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable Long id) {
        AlbumResponse response = albumService.getAlbumById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all albums")
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        List<AlbumResponse> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }
}