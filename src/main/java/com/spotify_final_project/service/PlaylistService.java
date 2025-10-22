package com.spotify_final_project.service;

import com.spotify_final_project.dto.playlist.CreatePlaylistRequest;
import com.spotify_final_project.dto.playlist.PlaylistResponse;
import com.spotify_final_project.exception.auth.AuthenticationException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.playlist.PlaylistNotFoundException;
import com.spotify_final_project.mappers.PlaylistMapper;
import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.PlaylistRepository;
import com.spotify_final_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    public PlaylistResponse createPlaylist(CreatePlaylistRequest request, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Playlist playlist = PlaylistMapper.mapToEntity(request);
        playlist.setOwner(owner);

        Playlist saved = playlistRepository.save(playlist);
        return PlaylistMapper.mapToResponse(saved);
    }

    public PlaylistResponse updatePlaylist(Long playlistId, CreatePlaylistRequest request, Long requesterId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!playlist.getOwner().getId().equals(requesterId) && !requester.getRole().name().equals("ADMIN")) {
            throw new AuthenticationException("You are not authorized to update this playlist");
        }

        playlist.setName(request.getName())
                .setDescription(request.getDescription());

        Playlist updated = playlistRepository.save(playlist);
        return PlaylistMapper.mapToResponse(updated);
    }

    public void deletePlaylist(Long playlistId, Long requesterId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!playlist.getOwner().getId().equals(requesterId) && !requester.getRole().name().equals("ADMIN")) {
            throw new AuthenticationException("You are not authorized to delete this playlist");
        }

        playlistRepository.delete(playlist);
    }

    public PlaylistResponse getPlaylistById(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));

        return PlaylistMapper.mapToResponse(playlist);
    }

    public List<PlaylistResponse> getAllPlaylists() {
        return playlistRepository.findAll()
                .stream()
                .map(PlaylistMapper::mapToResponse)
                .collect(Collectors.toList());
    }
}

