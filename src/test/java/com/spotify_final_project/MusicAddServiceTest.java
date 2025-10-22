package com.spotify_final_project;

import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.music.MusicNotFoundException;
import com.spotify_final_project.exception.playlist.PlaylistNotFoundException;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.PlaylistRepository;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.MusicAddService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicAddServiceTest {

    private AlbumRepository albumRepository;
    private PlaylistRepository playlistRepository;
    private MusicRepository musicRepository;
    private UserRepository userRepository;
    private MusicAddService musicAddService;

    @BeforeEach
    void setUp() {
        albumRepository = mock(AlbumRepository.class);
        playlistRepository = mock(PlaylistRepository.class);
        musicRepository = mock(MusicRepository.class);
        userRepository = mock(UserRepository.class);
        musicAddService = new MusicAddService(albumRepository, playlistRepository, musicRepository, userRepository);
    }

    @Test
    void addMusicToPlaylist_ShouldAddMusic_WhenAllExist() {
        Playlist playlist = new Playlist();
        playlist.setTracks(new ArrayList<>());
        Music music = new Music();

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(musicRepository.findById(2L)).thenReturn(Optional.of(music));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mock(com.spotify_final_project.model.User.class)));
        when(playlistRepository.save(playlist)).thenReturn(playlist);

        Playlist result = musicAddService.addMusicToPlaylist(1L, 2L, 3L);

        assertTrue(result.getTracks().contains(music));
        verify(playlistRepository, times(1)).save(playlist);
    }

    @Test
    void addMusicToPlaylist_ShouldThrow_WhenPlaylistNotFound() {
        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PlaylistNotFoundException.class, () -> musicAddService.addMusicToPlaylist(1L, 2L, 3L));
    }

    @Test
    void addMusicToPlaylist_ShouldThrow_WhenMusicNotFound() {
        Playlist playlist = new Playlist();
        playlist.setTracks(new ArrayList<>());
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(musicRepository.findById(2L)).thenReturn(Optional.empty());
        when(userRepository.findById(3L)).thenReturn(Optional.of(mock(com.spotify_final_project.model.User.class)));

        assertThrows(MusicNotFoundException.class, () -> musicAddService.addMusicToPlaylist(1L, 2L, 3L));
    }

    @Test
    void addMusicToPlaylist_ShouldThrow_WhenUserNotFound() {
        Playlist playlist = new Playlist();
        playlist.setTracks(new ArrayList<>());
        Music music = new Music();

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(musicRepository.findById(2L)).thenReturn(Optional.of(music));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> musicAddService.addMusicToPlaylist(1L, 2L, 3L));
    }

    @Test
    void addMusicToPlaylist_ShouldNotAddDuplicateMusic() {
        Playlist playlist = new Playlist();
        Music music = new Music();
        playlist.setTracks(new ArrayList<>());
        playlist.getTracks().add(music);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(musicRepository.findById(2L)).thenReturn(Optional.of(music));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mock(com.spotify_final_project.model.User.class)));
        when(playlistRepository.save(playlist)).thenReturn(playlist);

        Playlist result = musicAddService.addMusicToPlaylist(1L, 2L, 3L);

        assertEquals(1, result.getTracks().size()); // ensure music not duplicated
    }
}
