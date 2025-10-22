package com.spotify_final_project.controller;

import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.model.User;
import com.spotify_final_project.service.AdminService;
import com.spotify_final_project.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthorizationService authorizationService;

    // ===== User Management =====
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted with id: " + userId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/promote/{userId}")
    @Operation(summary = "Promote a user to admin")
    public ResponseEntity<String> promoteToAdmin(@PathVariable Long userId) {
        User promoted = adminService.promoteToAdmin(userId);
        return ResponseEntity.ok("User promoted to admin: " + promoted.getUsername());
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/user/{userId}/block")
    @Operation(summary = "Block a user")
    public ResponseEntity<User> blockUser(@PathVariable Long userId) {
        User blockedUser = adminService.blockUser(userId);
        return ResponseEntity.ok(blockedUser);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/user/{userId}/unblock")
    @Operation(summary = "Unblock a user")
    public ResponseEntity<User> unblockUser(@PathVariable Long userId) {
        User unblockedUser = adminService.unblockUser(userId);
        return ResponseEntity.ok(unblockedUser);
    }

    // ===== Album Management =====
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/album/{albumId}")
    @Operation(summary = "Delete an album")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long albumId) {
        adminService.deleteAlbum(albumId);
        return ResponseEntity.ok("Album deleted with id: " + albumId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/album")
    @Operation(summary = "Create an album for an artist")
    public ResponseEntity<Album> createAlbum(@RequestBody Album album, @RequestParam Long artistId) {
        User artist = adminService.getAllUsers().stream()
                .filter(u -> u.getId().equals(artistId))
                .findFirst()
                .orElseThrow();
        Album saved = adminService.createAlbum(album, artist);
        return ResponseEntity.ok(saved);
    }

    // ===== Music Management =====
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/music/{musicId}")
    @Operation(summary = "Delete a music track")
    public ResponseEntity<String> deleteMusic(@PathVariable Long musicId) {
        adminService.deleteMusic(musicId);
        return ResponseEntity.ok("Music deleted with id: " + musicId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/music")
    @Operation(summary = "Create a music track for an artist")
    public ResponseEntity<Music> createMusic(@RequestBody Music music, @RequestParam Long artistId) {
        User artist = adminService.getAllUsers().stream()
                .filter(u -> u.getId().equals(artistId))
                .findFirst()
                .orElseThrow();
        Music saved = adminService.createMusic(music, artist);
        return ResponseEntity.ok(saved);
    }

    // ===== Playlist Management =====
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/playlist/{playlistId}")
    @Operation(summary = "Delete any playlist")
    public ResponseEntity<String> deletePlaylist(@PathVariable Long playlistId) {
        adminService.deletePlaylist(playlistId);
        return ResponseEntity.ok("Deleted playlist with ID: " + playlistId);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/playlist/{playlistId}")
    @Operation(summary = "Update any playlist")
    public ResponseEntity<Playlist> updatePlaylist(
            @PathVariable Long playlistId,
            @RequestBody Playlist updatedPlaylist) {
        Playlist playlist = adminService.updatePlaylist(playlistId, updatedPlaylist);
        return ResponseEntity.ok(playlist);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create/{adminId}")
    @Operation(summary = "Create a playlist as admin")
    public ResponseEntity<Playlist> createAdminPlaylist(
            @RequestBody Playlist playlist,
            HttpServletRequest request) {

        User admin = authorizationService.getLoggedInUser(request);

        Playlist created = adminService.createAdminPlaylist(playlist, admin.getId());
        return ResponseEntity.ok(created);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get all playlists")
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        return ResponseEntity.ok(adminService.getAllPlaylists());
    }

}
