package com.spotify_final_project.service;

import com.spotify_final_project.dto.album.AlbumRequest;
import com.spotify_final_project.dto.album.AlbumResponse;
import com.spotify_final_project.exception.album.AlbumNotFoundException;
import com.spotify_final_project.exception.auth.AuthenticationException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.mappers.AlbumMapper;
import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public AlbumResponse createAlbum(AlbumRequest request, Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new AlbumNotFoundException("Artist not found"));

        Album album = AlbumMapper.mapToEntity(request);
        album.setArtist(artist);

        Album savedAlbum = albumRepository.save(album);
        return AlbumMapper.mapToResponse(savedAlbum);
    }

    public AlbumResponse updateAlbum(Long albumId, AlbumRequest request, Long requesterId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new UserNotFoundException("Album not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new AlbumNotFoundException("User not found"));

        if (!album.getArtist().getId().equals(requesterId) && !requester.getRole().name().equals("ADMIN")) {
            throw new AuthenticationException("You are not authorized to update this album");
        }

        album.setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setReleaseDate(request.getReleaseDate())
                .setGenre(request.getGenre());

        Album updatedAlbum = albumRepository.save(album);
        return AlbumMapper.mapToResponse(updatedAlbum);
    }

    public void deleteAlbum(Long albumId, Long requesterId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException("Album not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new AlbumNotFoundException("User not found"));

        if (!album.getArtist().getId().equals(requesterId) && !requester.getRole().name().equals("ADMIN")) {
            throw new AuthenticationException("You are not authorized to delete this album");
        }

        albumRepository.delete(album);
    }

    public AlbumResponse getAlbumById(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException("Album not found"));

        return AlbumMapper.mapToResponse(album);
    }

    public List<AlbumResponse> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(AlbumMapper::mapToResponse)
                .collect(Collectors.toList());
    }
}