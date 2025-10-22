package com.spotify_final_project.service;

import com.spotify_final_project.dto.music.MusicRequest;
import com.spotify_final_project.dto.music.MusicResponse;
import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.album.AlbumNotFoundException;
import com.spotify_final_project.exception.auth.InvalidCredentialsException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.music.MusicNotFoundException;
import com.spotify_final_project.mappers.MusicMapper;
import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;


    public MusicResponse createMusic(MusicRequest request, Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException("Artist not found"));
        if (Role.valueOf(String.valueOf(artist.getRole())) != Role.ARTIST) {
            throw new InvalidCredentialsException("Only artists can upload music");
        }

        Album album = albumRepository.findById(request.getAlbumId())
                .orElseThrow(() -> new AlbumNotFoundException("Album not found"));

        Music music = MusicMapper.mapToEntity(request);
        music.setArtist(artist);
        music.setAlbum(album);

        music = musicRepository.save(music);

        return MusicMapper.mapToResponse(music);
    }


    @Transactional
    public MusicResponse updateMusic(Long musicId, MusicRequest request, Long artistId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new MusicNotFoundException("Music not found"));

        if (!music.getArtist().getId().equals(artistId)) {
            throw new InvalidCredentialsException("You can only update your own music");
        }

        music.setTitle(request.getTitle());
        music.setGenre(request.getGenre());
        music.setDuration(request.getDuration());

        Music updated = musicRepository.save(music);

        return MusicMapper.mapToResponse(updated);
    }

    public void deleteMusic(Long musicId, Long userId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new MusicNotFoundException("Music not found"));

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean isOwner = music.getArtist().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().equals(Role.ADMIN.name());

        if (!isOwner && !isAdmin) {
            throw new InvalidCredentialsException("You do not have permission to delete this music");
        }

        musicRepository.delete(music);
    }

    public MusicResponse getMusicById(Long musicId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new MusicNotFoundException("Music not found"));

        return MusicMapper.mapToResponse(music);
    }


    public List<MusicResponse> getAllMusics() {
        return musicRepository.findAll()
                .stream()
                .map(MusicMapper::mapToResponse)
                .toList();
    }


    public Music getMusicEntityById(Long id) {
        return musicRepository.findById(id)
                .orElseThrow(() -> new MusicNotFoundException("Music not found"));
    }

    public List<Music> searchByTitle(String title) {
        return musicRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Music> searchByArtist(String artistUsername) {
        return musicRepository.findByArtist_UsernameContainingIgnoreCase(artistUsername);
    }

    public List<Music> searchByTitleOrArtist(String keyword) {
        return musicRepository.findByTitleContainingIgnoreCaseOrArtist_UsernameContainingIgnoreCase(keyword, keyword);
    }

    public List<Music> getMusicByArtist(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException("Artist not found"));

        return musicRepository.findByArtist(artist);
    }

    public List<Music> getMusicByGenre(GenreType genre) {
        return musicRepository.findByGenre(genre);
    }

}
