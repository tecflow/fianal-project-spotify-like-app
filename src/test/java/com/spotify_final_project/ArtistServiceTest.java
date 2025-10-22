package com.spotify_final_project;

import com.spotify_final_project.dto.Artist.ArtistProfileResponse;
import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArtistServiceTest {

    private UserRepository userRepository;
    private MusicRepository musicRepository;
    private ArtistService artistService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        musicRepository = mock(MusicRepository.class);
        artistService = new ArtistService(userRepository, musicRepository);
    }

    @Test
    void getArtistProfile_ShouldReturnProfile_WhenArtistExists() {
        User artist = new User();
        artist.setId(1L);
        artist.setUsername("artist1");
        artist.setFirstName("John");
        artist.setLastName("Doe");

        Music music1 = new Music();
        music1.setGenre(GenreType.ROCK);
        Music music2 = new Music();
        music2.setGenre(GenreType.POP);

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("artist2");

        Music otherMusic = new Music();
        otherMusic.setGenre(GenreType.ROCK);

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(musicRepository.findAllByArtist(artist)).thenReturn(List.of(music1, music2));
        when(userRepository.findAll()).thenReturn(List.of(artist, otherUser));
        when(musicRepository.findAllByArtist(otherUser)).thenReturn(List.of(otherMusic));

        ArtistProfileResponse response = artistService.getArtistProfile(1L);

        assertEquals(1L, response.getArtistId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals(Set.of(GenreType.ROCK, GenreType.POP), response.getGenres());
        assertEquals(List.of("artist2"), response.getSimilarArtists());
    }

    @Test
    void getArtistProfile_ShouldThrow_WhenArtistNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> artistService.getArtistProfile(1L));
        assertEquals("Artist not found", exception.getMessage());
    }

    @Test
    void getArtistProfile_ShouldReturnEmptyGenresAndSimilar_WhenNoMusic() {
        User artist = new User();
        artist.setId(1L);
        artist.setUsername("artist1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(musicRepository.findAllByArtist(artist)).thenReturn(List.of());
        when(userRepository.findAll()).thenReturn(List.of(artist));

        ArtistProfileResponse response = artistService.getArtistProfile(1L);

        assertTrue(response.getGenres().isEmpty());
        assertTrue(response.getSimilarArtists().isEmpty());
    }

    @Test
    void getArtistProfile_ShouldReturnEmptySimilarArtists_WhenNoOverlap() {
        User artist = new User();
        artist.setId(1L);

        Music music1 = new Music();
        music1.setGenre(GenreType.ROCK);

        User otherUser = new User();
        otherUser.setId(2L);

        Music otherMusic = new Music();
        otherMusic.setGenre(GenreType.POP);

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(musicRepository.findAllByArtist(artist)).thenReturn(List.of(music1));
        when(userRepository.findAll()).thenReturn(List.of(artist, otherUser));
        when(musicRepository.findAllByArtist(otherUser)).thenReturn(List.of(otherMusic));

        ArtistProfileResponse response = artistService.getArtistProfile(1L);

        assertEquals(List.of(), response.getSimilarArtists());
    }
}
