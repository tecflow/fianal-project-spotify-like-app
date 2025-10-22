package com.spotify_final_project;

import com.spotify_final_project.enums.AccountStatus;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.album.AlbumNotFoundException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.playlist.PlaylistNotFoundException;
import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.PlaylistRepository;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private UserRepository userRepository;
    private AlbumRepository albumRepository;
    private MusicRepository musicRepository;
    private PlaylistRepository playlistRepository;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        albumRepository = mock(AlbumRepository.class);
        musicRepository = mock(MusicRepository.class);
        playlistRepository = mock(PlaylistRepository.class);
        adminService = new AdminService(userRepository, albumRepository, musicRepository, playlistRepository);
    }

    // ===== User Management =====

    @Test
    void deleteUser_ShouldDeleteUserAndAllAssociatedData() {
        User user = new User();
        user.setId(1L);
        Music music1 = new Music();
        music1.setId(101L);
        Music music2 = new Music();
        music2.setId(102L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(musicRepository.findAllByArtist(user)).thenReturn(List.of(music1, music2));

        adminService.deleteUser(1L);

        // Verify all associated music removed from playlists
        verify(playlistRepository, times(1)).removeMusicFromAllPlaylists(101L);
        verify(playlistRepository, times(1)).removeMusicFromAllPlaylists(102L);

        verify(musicRepository, times(1)).deleteAllByArtist(user);
        verify(albumRepository, times(1)).deleteAllByArtist(user);
        verify(playlistRepository, times(1)).deleteAllByOwner(user);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.deleteUser(1L));
    }

    @Test
    void promoteToAdmin_ShouldSetRoleAndStatus() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ARTIST);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = adminService.promoteToAdmin(1L);
        assertEquals(Role.ADMIN, updated.getRole());
        assertEquals(AccountStatus.ACTIVE, updated.getStatus());
        assertTrue(updated.isVerified());
    }

    @Test
    void promoteToAdmin_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.promoteToAdmin(1L));
    }

    @Test
    void blockUser_ShouldSetStatusBlocked() {
        User user = new User();
        user.setId(1L);
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User blocked = adminService.blockUser(1L);
        assertEquals(AccountStatus.BLOCKED, blocked.getStatus());
    }

    @Test
    void blockUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.blockUser(1L));
    }

    @Test
    void unblockUser_ShouldSetStatusActive() {
        User user = new User();
        user.setId(1L);
        user.setStatus(AccountStatus.BLOCKED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User active = adminService.unblockUser(1L);
        assertEquals(AccountStatus.ACTIVE, active.getStatus());
    }

    @Test
    void unblockUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.unblockUser(1L));
    }

    // ===== Album Management =====

    @Test
    void deleteAlbum_ShouldDelete_WhenFound() {
        Album album = new Album();
        album.setId(1L);
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        adminService.deleteAlbum(1L);
        verify(albumRepository).delete(album);
    }

    @Test
    void deleteAlbum_ShouldThrow_WhenNotFound() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AlbumNotFoundException.class, () -> adminService.deleteAlbum(1L));
    }

    @Test
    void updateAlbum_ShouldUpdate_WhenFound() {
        Album album = new Album();
        album.setId(1L);
        when(albumRepository.existsById(1L)).thenReturn(true);
        when(albumRepository.save(album)).thenReturn(album);

        Album updated = adminService.updateAlbum(album);
        assertEquals(album, updated);
    }

    @Test
    void updateAlbum_ShouldThrow_WhenNotFound() {
        Album album = new Album();
        album.setId(1L);
        when(albumRepository.existsById(1L)).thenReturn(false);
        assertThrows(AlbumNotFoundException.class, () -> adminService.updateAlbum(album));
    }

    @Test
    void createAlbum_ShouldSaveWithArtist() {
        Album album = new Album();
        User artist = new User();
        when(albumRepository.save(album)).thenReturn(album);
        Album saved = adminService.createAlbum(album, artist);
        assertEquals(artist, saved.getArtist());
    }

    // ===== Music Management =====

    @Test
    void deleteMusic_ShouldDelete_WhenFound() {
        Music music = new Music();
        music.setId(1L);
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        adminService.deleteMusic(1L);
        verify(musicRepository).delete(music);
    }

    @Test
    void deleteMusic_ShouldThrow_WhenNotFound() {
        when(musicRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminService.deleteMusic(1L));
    }

    @Test
    void createMusic_ShouldSaveWithArtist() {
        Music music = new Music();
        User artist = new User();
        when(musicRepository.save(music)).thenReturn(music);
        Music saved = adminService.createMusic(music, artist);
        assertEquals(artist, saved.getArtist());
    }

    // ===== Playlist Management =====

    @Test
    void deletePlaylist_ShouldDelete_WhenFound() {
        Playlist playlist = new Playlist();
        playlist.setId(1L);
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        adminService.deletePlaylist(1L);
        verify(playlistRepository).delete(playlist);
    }

    @Test
    void deletePlaylist_ShouldThrow_WhenNotFound() {
        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PlaylistNotFoundException.class, () -> adminService.deletePlaylist(1L));
    }

    @Test
    void updatePlaylist_ShouldUpdatePlaylist() {
        Playlist oldPlaylist = new Playlist();
        oldPlaylist.setId(1L);
        oldPlaylist.setName("Old");
        oldPlaylist.setDescription("Old Desc");

        Playlist updatedPlaylist = new Playlist();
        updatedPlaylist.setName("New");
        updatedPlaylist.setDescription("New Desc");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(oldPlaylist));
        when(playlistRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Playlist result = adminService.updatePlaylist(1L, updatedPlaylist);
        assertEquals("New", result.getName());
        assertEquals("New Desc", result.getDescription());
    }

    @Test
    void createAdminPlaylist_ShouldSave_WhenAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Playlist playlist = new Playlist();

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(playlistRepository.save(playlist)).thenReturn(playlist);

        Playlist saved = adminService.createAdminPlaylist(playlist, 1L);
        assertEquals(admin, saved.getOwner());
    }

    @Test
    void createAdminPlaylist_ShouldThrow_WhenNotAdmin() {
        User user = new User();
        user.setId(2L);
        user.setRole(Role.ARTIST);

        Playlist playlist = new Playlist();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(UserNotFoundException.class, () -> adminService.createAdminPlaylist(playlist, 2L));
    }
}
