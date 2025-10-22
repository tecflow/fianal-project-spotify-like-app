package com.spotify_final_project.service;

import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Listen;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.ListenRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestionPlaylistService {

    private final ListenRepository listenRepository;
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;

    public Map<GenreType, List<Music>> generateSessionPlaylists(Long userId, int page, int pageSizePerGenre) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Listen> listens = listenRepository.findAllByListener(user);

        if (listens.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<GenreType, Long> genreCounts = listens.stream()
                .collect(Collectors.groupingBy(l -> l.getMusic().getGenre(), Collectors.summingLong(Listen::getCount)));

        List<GenreType> topGenres = genreCounts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        Map<GenreType, List<Music>> playlists = new LinkedHashMap<>();
        for (GenreType genre : topGenres) {
            List<Music> songs = musicRepository.findAllByGenre(genre, PageRequest.of(page, pageSizePerGenre));
            playlists.put(genre, songs);
        }

        return playlists;
    }
}
