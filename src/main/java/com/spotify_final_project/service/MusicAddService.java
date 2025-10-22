package com.spotify_final_project.service;

import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.music.MusicNotFoundException;
import com.spotify_final_project.exception.playlist.PlaylistNotFoundException;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.PlaylistRepository;
import com.spotify_final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class MusicAddService {
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;



    public Playlist addMusicToPlaylist(Long playlistId, Long musicId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new MusicNotFoundException("Music not found"));
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!playlist.getTracks().contains(music)) {
            playlist.getTracks().add(music);
        }

        return playlistRepository.save(playlist);
    }
}
