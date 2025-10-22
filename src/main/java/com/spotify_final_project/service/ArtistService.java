package com.spotify_final_project.service;

import com.spotify_final_project.dto.Artist.ArtistProfileResponse;
import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final UserRepository userRepository;
    private final MusicRepository musicRepository;

    public ArtistProfileResponse getArtistProfile(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        // Get genres of this artist
        Set<GenreType> artistGenres = musicRepository.findAllByArtist(artist).stream()
                .map(Music::getGenre)
                .collect(Collectors.toSet());

        // Find similar artists
        List<String> similarArtists = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(artistId)) // exclude self
                .filter(u -> musicRepository.findAllByArtist(u).stream()
                        .anyMatch(m -> artistGenres.contains(m.getGenre())))
                .map(User::getUsername)
                .distinct()
                .limit(10)
                .toList();

        return ArtistProfileResponse.builder()
                .artistId(artist.getId())
                .firstName(artist.getFirstName())
                .lastName(artist.getLastName())
                .username(artist.getUsername())
                .genres(artistGenres)
                .similarArtists(similarArtists)
                .build();
    }
}
