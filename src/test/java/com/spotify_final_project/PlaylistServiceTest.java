package com.spotify_final_project;

import com.spotify_final_project.dto.playlist.CreatePlaylistRequest;
import com.spotify_final_project.dto.playlist.PlaylistResponse;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.auth.AuthenticationException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.playlist.PlaylistNotFoundException;
import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.PlaylistRepository;
import com.spotify_final_project.repository.UserRepository;
import com.spotify_final_project.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.spotify_final_project.enums.Role.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaylistServiceTest {

    private PlaylistRepository playlistRepository;
    private UserRepository userRepository;
    private PlaylistService playlistService;

    @BeforeEach
    void setUp() {
        playlistRepository = mock(PlaylistRepository.class);
        userRepository = mock(UserRepository.class);
        playlistService = new PlaylistService(playlistRepository, userRepository);
    }

    @Test
    void createPlaylist_ShouldReturnPlaylistResponse_WhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("ownerUser");

        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("My Playlist");
        request.setDescription("Description");

        Playlist savedPlaylist = new Playlist();
        savedPlaylist.setId(1L);
        savedPlaylist.setName("My Playlist");
        savedPlaylist.setDescription("Description");
        savedPlaylist.setOwner(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(playlistRepository.save(any())).thenReturn(savedPlaylist);

        PlaylistResponse response = playlistService.createPlaylist(request, 1L);

        assertEquals(1L, response.getId());
        assertEquals("My Playlist", response.getName());
        assertEquals("Description", response.getDescription());
        assertEquals("ownerUser", response.getOwnerUsername());
        verify(playlistRepository, times(1)).save(any());
    }

    @Test
    void createPlaylist_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        CreatePlaylistRequest request = new CreatePlaylistRequest();
        assertThrows(UserNotFoundException.class, () -> playlistService.createPlaylist(request, 1L));
    }

    @Test
    void updatePlaylist_ShouldUpdate_WhenOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("ownerUser");

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setName("Old Name");
        playlist.setOwner(owner);

        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("New Name");
        request.setDescription("New Description");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(playlistRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PlaylistResponse response = playlistService.updatePlaylist(1L, request, 1L);

        assertEquals("New Name", response.getName());
        assertEquals("New Description", response.getDescription());
        assertEquals("ownerUser", response.getOwnerUsername());
    }

    @Test
    void updatePlaylist_ShouldUpdate_WhenAdmin() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("ownerUser");

        User admin = new User();
        admin.setId(2L);
        admin.setUsername("adminUser");
        admin.setRole(ADMIN); // assuming Role enum exists

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setName("Old Name");
        playlist.setOwner(owner);

        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("New Name");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(playlistRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PlaylistResponse response = playlistService.updatePlaylist(1L, request, 2L);
        assertEquals("New Name", response.getName());
        assertEquals("ownerUser", response.getOwnerUsername());
    }

    @Test
    void updatePlaylist_ShouldThrow_WhenNotOwnerOrAdmin() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("ownerUser");

        User requester = new User();
        requester.setId(2L);
        requester.setUsername("requesterUser");
        requester.setRole(Role.LISTENER); // ✅ FIX — must not be null

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(owner);

        CreatePlaylistRequest request = new CreatePlaylistRequest();
        request.setName("New Name");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(userRepository.findById(2L)).thenReturn(Optional.of(requester));

        assertThrows(AuthenticationException.class, () ->
                playlistService.updatePlaylist(1L, request, 2L)
        );
    }


    @Test
    void deletePlaylist_ShouldDelete_WhenOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("ownerUser");

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(owner);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        playlistService.deletePlaylist(1L, 1L);
        verify(playlistRepository, times(1)).delete(playlist);
    }

    @Test
    void deletePlaylist_ShouldDelete_WhenAdmin() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("ownerUser");

        User admin = new User();
        admin.setId(2L);
        admin.setUsername("adminUser");
        admin.setRole(ADMIN);

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setOwner(owner);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        playlistService.deletePlaylist(1L, 2L);
        verify(playlistRepository, times(1)).delete(playlist);
    }

    @Test
    void getPlaylistById_ShouldReturnResponse_WhenFound() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("ownerUser");

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setName("My Playlist");
        playlist.setOwner(owner);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        PlaylistResponse response = playlistService.getPlaylistById(1L);

        assertEquals(1L, response.getId());
        assertEquals("My Playlist", response.getName());
        assertEquals("ownerUser", response.getOwnerUsername());
    }

    @Test
    void getAllPlaylists_ShouldReturnList() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("ownerUser");

        Playlist p1 = new Playlist();
        p1.setId(1L);
        p1.setOwner(owner);

        Playlist p2 = new Playlist();
        p2.setId(2L);
        p2.setOwner(owner);

        when(playlistRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PlaylistResponse> playlists = playlistService.getAllPlaylists();

        assertEquals(2, playlists.size());
        assertEquals("ownerUser", playlists.get(0).getOwnerUsername());
        assertEquals("ownerUser", playlists.get(1).getOwnerUsername());
    }

    @Test
    void getAllPlaylists_ShouldReturnEmptyList_WhenNoPlaylists() {
        when(playlistRepository.findAll()).thenReturn(List.of());

        List<PlaylistResponse> playlists = playlistService.getAllPlaylists();
        assertTrue(playlists.isEmpty());
    }
}
