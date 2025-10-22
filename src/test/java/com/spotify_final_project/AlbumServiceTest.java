package com.spotify_final_project;

import com.spotify_final_project.dto.album.AlbumRequest;
import com.spotify_final_project.dto.album.AlbumResponse;
import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.album.AlbumNotFoundException;
import com.spotify_final_project.exception.auth.AuthenticationException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlbumServiceTest {

    private AlbumRepository albumRepository;
    private UserRepository userRepository;
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        albumRepository = mock(AlbumRepository.class);
        userRepository = mock(UserRepository.class);
        albumService = new AlbumService(albumRepository, userRepository);
    }

    @Test
    void createAlbum_ShouldReturnAlbumResponse_WhenArtistExists() {
        AlbumRequest request = new AlbumRequest();
        request.setTitle("Album1");
        request.setDescription("Desc");
        request.setGenre(GenreType.ROCK);
        request.setReleaseDate(LocalDate.of(2025, 10, 22));

        User artist = new User();
        artist.setId(1L);

        Album savedAlbum = new Album();
        savedAlbum.setId(10L);
        savedAlbum.setArtist(artist);
        savedAlbum.setTitle("Album1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(albumRepository.save(any(Album.class))).thenReturn(savedAlbum);

        AlbumResponse response = albumService.createAlbum(request, 1L);

        assertEquals(10L, response.getId());
        assertEquals("Album1", response.getTitle());
        verify(albumRepository).save(any(Album.class));
    }



    @Test
    void updateAlbum_ShouldUpdateSuccessfully_WhenAuthorized() {
        User artist = new User();
        artist.setId(1L);
        Album album = new Album();
        album.setId(5L);
        album.setArtist(artist);

        AlbumRequest request = new AlbumRequest();
        request.setTitle("NewTitle");
        request.setDescription("NewDesc");
        request.setGenre(GenreType.POP);
        request.setReleaseDate(LocalDate.now());

        when(albumRepository.findById(5L)).thenReturn(Optional.of(album));
        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(albumRepository.save(album)).thenReturn(album);

        AlbumResponse response = albumService.updateAlbum(5L, request, 1L);
        assertEquals("NewTitle", response.getTitle());
    }

    @Test
    void updateAlbum_ShouldThrow_WhenUnauthorized() {
        User artist = new User();
        artist.setId(1L);
        User requester = new User();
        requester.setId(2L);
        requester.setRole(null); // not admin
        Album album = new Album();
        album.setArtist(artist);

        AlbumRequest request = new AlbumRequest();

        when(albumRepository.findById(5L)).thenReturn(Optional.of(album));
        when(userRepository.findById(2L)).thenReturn(Optional.of(requester));

        assertThrows(AuthenticationException.class, () -> albumService.updateAlbum(5L, request, 2L));
    }

    @Test
    void deleteAlbum_ShouldDeleteSuccessfully_WhenAuthorized() {
        User artist = new User();
        artist.setId(1L);
        Album album = new Album();
        album.setArtist(artist);

        when(albumRepository.findById(5L)).thenReturn(Optional.of(album));
        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));

        albumService.deleteAlbum(5L, 1L);
        verify(albumRepository).delete(album);
    }

    @Test
    void getAlbumById_ShouldReturnAlbum_WhenFound() {
        Album album = new Album();
        album.setId(5L);
        when(albumRepository.findById(5L)).thenReturn(Optional.of(album));

        AlbumResponse response = albumService.getAlbumById(5L);
        assertEquals(5L, response.getId());
    }

    @Test
    void getAlbumById_ShouldThrow_WhenNotFound() {
        when(albumRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(AlbumNotFoundException.class, () -> albumService.getAlbumById(5L));
    }

    @Test
    void getAllAlbums_ShouldReturnList() {
        Album album1 = new Album();
        album1.setId(1L);
        Album album2 = new Album();
        album2.setId(2L);

        when(albumRepository.findAll()).thenReturn(List.of(album1, album2));

        List<AlbumResponse> albums = albumService.getAllAlbums();
        assertEquals(2, albums.size());
    }

    @Test
    void deleteAlbum_ShouldThrow_WhenUnauthorized() {
        User artist = new User();
        artist.setId(1L);
        User requester = new User();
        requester.setId(2L);
        requester.setRole(null); // not admin
        Album album = new Album();
        album.setArtist(artist);

        when(albumRepository.findById(5L)).thenReturn(Optional.of(album));
        when(userRepository.findById(2L)).thenReturn(Optional.of(requester));

        assertThrows(AuthenticationException.class, () -> albumService.deleteAlbum(5L, 2L));
    }

    @Test
    void updateAlbum_ShouldSucceed_WhenAdmin() {
        User artist = new User();
        artist.setId(1L);
        Album album = new Album();
        album.setId(5L);
        album.setArtist(artist);

        User admin = new User();
        admin.setId(2L);
        admin.setRole(Role.ADMIN);

        AlbumRequest request = new AlbumRequest();
        request.setTitle("AdminUpdated");

        when(albumRepository.findById(5L)).thenReturn(Optional.of(album));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(albumRepository.save(album)).thenReturn(album);

        AlbumResponse response = albumService.updateAlbum(5L, request, 2L);
        assertEquals("AdminUpdated", response.getTitle());
    }

    @Test
    void createAlbum_ShouldThrow_WhenArtistNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        AlbumRequest request = new AlbumRequest();
        assertThrows(AlbumNotFoundException.class, () -> albumService.createAlbum(request, 1L));
    }

    @Test
    void updateAlbum_ShouldThrow_WhenAlbumNotFound() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        AlbumRequest request = new AlbumRequest();
        assertThrows(UserNotFoundException.class, () -> albumService.updateAlbum(1L, request, 1L));
    }

    @Test
    void updateAlbum_ShouldThrow_WhenUserNotFound() {
        Album album = new Album();
        album.setId(1L);
        User artist = new User();
        artist.setId(1L);
        album.setArtist(artist);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        AlbumRequest request = new AlbumRequest();
        assertThrows(AlbumNotFoundException.class, () -> albumService.updateAlbum(1L, request, 2L));
    }

    @Test
    void deleteAlbum_ShouldThrow_WhenAlbumNotFound() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AlbumNotFoundException.class, () -> albumService.deleteAlbum(1L, 1L));
    }

    @Test
    void deleteAlbum_ShouldThrow_WhenUserNotFound() {
        Album album = new Album();
        album.setId(1L);
        User artist = new User();
        artist.setId(1L);
        album.setArtist(artist);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AlbumNotFoundException.class, () -> albumService.deleteAlbum(1L, 2L));
    }

}
