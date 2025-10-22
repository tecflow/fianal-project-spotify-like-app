package com.spotify_final_project;

import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Listen;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.ListenRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.SuggestionPlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SuggestionPlaylistServiceTest {

    private ListenRepository listenRepository;
    private MusicRepository musicRepository;
    private UserRepository userRepository;
    private SuggestionPlaylistService suggestionService;

    @BeforeEach
    void setUp() {
        listenRepository = mock(ListenRepository.class);
        musicRepository = mock(MusicRepository.class);
        userRepository = mock(UserRepository.class);

        suggestionService = new SuggestionPlaylistService(listenRepository, musicRepository, userRepository);
    }

    @Test
    void generateSessionPlaylists_ShouldReturnEmpty_WhenNoListens() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(listenRepository.findAllByListener(user)).thenReturn(List.of());

        Map<GenreType, List<Music>> result = suggestionService.generateSessionPlaylists(1L, 0, 5);

        assertTrue(result.isEmpty());
    }

    @Test
    void generateSessionPlaylists_ShouldReturnTopGenres() {
        User user = new User();
        user.setId(1L);

        Music music1 = new Music();
        music1.setGenre(GenreType.ROCK);

        Music music2 = new Music();
        music2.setGenre(GenreType.POP);

        Listen listen1 = new Listen();
        listen1.setListener(user);
        listen1.setMusic(music1);
        listen1.setCount(5L);

        Listen listen2 = new Listen();
        listen2.setListener(user);
        listen2.setMusic(music2);
        listen2.setCount(2L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(listenRepository.findAllByListener(user)).thenReturn(List.of(listen1, listen2));

        when(musicRepository.findAllByGenre(eq(GenreType.ROCK), any(PageRequest.class)))
                .thenReturn(List.of(music1));
        when(musicRepository.findAllByGenre(eq(GenreType.POP), any(PageRequest.class)))
                .thenReturn(List.of(music2));

        Map<GenreType, List<Music>> result = suggestionService.generateSessionPlaylists(1L, 0, 5);

        assertEquals(2, result.size());
        assertEquals(List.of(music1), result.get(GenreType.ROCK));
        assertEquals(List.of(music2), result.get(GenreType.POP));
    }

    @Test
    void generateSessionPlaylists_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                suggestionService.generateSessionPlaylists(1L, 0, 5));

        assertEquals("User not found", exception.getMessage());
    }
}
