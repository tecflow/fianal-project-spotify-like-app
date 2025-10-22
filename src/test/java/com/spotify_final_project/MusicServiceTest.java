package com.spotify_final_project;

import com.spotify_final_project.dto.music.MusicRequest;
import com.spotify_final_project.dto.music.MusicResponse;
import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.album.AlbumNotFoundException;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.music.MusicNotFoundException;
import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.MusicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MusicServiceTest {

    private MusicRepository musicRepository;
    private UserRepository userRepository;
    private AlbumRepository albumRepository;
    private MusicService musicService;

    @BeforeEach
    void setUp() {
        musicRepository = mock(MusicRepository.class);
        userRepository = mock(UserRepository.class);
        albumRepository = mock(AlbumRepository.class);
        musicService = new MusicService(musicRepository, userRepository, albumRepository);
    }

    @Test
    void createMusic_ShouldReturnMusicResponse_WhenArtistExists() {
        User artist = new User();
        artist.setId(1L);
        artist.setRole(Role.ARTIST);

        Album album = new Album();
        album.setId(1L);

        MusicRequest request = new MusicRequest();
        request.setTitle("Song");
        request.setDuration(200);
        request.setGenre(GenreType.POP);
        request.setAlbumId(1L);

        Music savedMusic = new Music();
        savedMusic.setId(1L);
        savedMusic.setTitle("Song");
        savedMusic.setArtist(artist);
        savedMusic.setAlbum(album);

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(musicRepository.save(any())).thenReturn(savedMusic);

        MusicResponse response = musicService.createMusic(request, 1L);

        assertEquals("Song", response.getTitle());
        verify(musicRepository, times(1)).save(any());
    }

    @Test
    void createMusic_ShouldThrow_WhenUserNotArtist() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.LISTENER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MusicRequest request = new MusicRequest();
        assertThrows(InvalidCredentialsException.class, () -> musicService.createMusic(request, 1L));
    }

    @Test
    void createMusic_ShouldThrow_WhenAlbumNotFound() {
        User artist = new User();
        artist.setId(1L);
        artist.setRole(Role.ARTIST);

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        MusicRequest request = new MusicRequest();
        request.setAlbumId(1L);
        assertThrows(AlbumNotFoundException.class, () -> musicService.createMusic(request, 1L));
    }

    @Test
    void updateMusic_ShouldUpdate_WhenOwner() {
        User artist = new User();
        artist.setId(1L);

        Music music = new Music();
        music.setId(1L);
        music.setArtist(artist);
        music.setTitle("Old");

        MusicRequest request = new MusicRequest();
        request.setTitle("New");
        request.setDuration(300);
        request.setGenre(GenreType.ROCK);

        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        when(musicRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        MusicResponse updated = musicService.updateMusic(1L, request, 1L);
        assertEquals("New", updated.getTitle());
    }

    @Test
    void updateMusic_ShouldThrow_WhenNotOwner() {
        User artist = new User();
        artist.setId(1L);

        Music music = new Music();
        music.setId(1L);
        music.setArtist(artist);

        MusicRequest request = new MusicRequest();
        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        assertThrows(InvalidCredentialsException.class, () -> musicService.updateMusic(1L, request, 2L));
    }

    @Test
    void deleteMusic_ShouldDelete_WhenOwner() {
        User artist = new User();
        artist.setId(1L);
        artist.setRole(Role.ARTIST);

        Music music = new Music();
        music.setId(1L);
        music.setArtist(artist);

        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));

        musicService.deleteMusic(1L, 1L);
        verify(musicRepository, times(1)).delete(music);
    }

    @Test
    void deleteMusic_ShouldThrow_WhenNotOwnerOrAdmin() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole(Role.ARTIST);

        User requester = new User();
        requester.setId(2L);
        requester.setRole(Role.LISTENER);

        Music music = new Music();
        music.setId(1L);
        music.setArtist(owner);

        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));
        when(userRepository.findById(2L)).thenReturn(Optional.of(requester));

        assertThrows(InvalidCredentialsException.class, () -> musicService.deleteMusic(1L, 2L));
    }

    @Test
    void getMusicById_ShouldReturnMusic() {
        Music music = new Music();
        music.setId(1L);
        music.setTitle("Song");

        when(musicRepository.findById(1L)).thenReturn(Optional.of(music));

        MusicResponse response = musicService.getMusicById(1L);
        assertEquals("Song", response.getTitle());
    }

    @Test
    void getAllMusics_ShouldReturnList() {
        Music m1 = new Music();
        Music m2 = new Music();

        when(musicRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MusicResponse> list = musicService.getAllMusics();
        assertEquals(2, list.size());
    }

    @Test
    void searchByTitle_ShouldReturnList() {
        Music m1 = new Music();
        when(musicRepository.findByTitleContainingIgnoreCase("song")).thenReturn(List.of(m1));
        List<Music> result = musicService.searchByTitle("song");
        assertEquals(1, result.size());
    }

    @Test
    void searchByArtist_ShouldReturnList() {
        Music m1 = new Music();
        when(musicRepository.findByArtist_UsernameContainingIgnoreCase("artist")).thenReturn(List.of(m1));
        List<Music> result = musicService.searchByArtist("artist");
        assertEquals(1, result.size());
    }

    @Test
    void searchByTitleOrArtist_ShouldReturnList() {
        Music m1 = new Music();
        when(musicRepository.findByTitleContainingIgnoreCaseOrArtist_UsernameContainingIgnoreCase("key", "key"))
                .thenReturn(List.of(m1));
        List<Music> result = musicService.searchByTitleOrArtist("key");
        assertEquals(1, result.size());
    }
}
