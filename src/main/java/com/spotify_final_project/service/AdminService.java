package com.spotify_final_project.service;

import com.spotify_final_project.enums.AccountStatus;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.exception.album.AlbumNotFoundException;
import com.spotify_final_project.exception.auth.UserNotFoundException;
import com.spotify_final_project.exception.playlist.PlaylistNotFoundException;
import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.AlbumRepository;
import com.spotify_final_project.repository.MusicRepository;
import com.spotify_final_project.repository.PlaylistRepository;
import com.spotify_final_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminService {


    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        List<Music> userMusic = musicRepository.findAllByArtist(user);
        for (Music music : userMusic) {
            playlistRepository.removeMusicFromAllPlaylists(music.getId());
        }

        musicRepository.deleteAllByArtist(user);

        albumRepository.deleteAllByArtist(user);

        playlistRepository.deleteAllByOwner(user);

        userRepository.delete(user);
    }


    public User promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        user.setRole(Role.ADMIN);
        user.setStatus(AccountStatus.ACTIVE);
        user.setVerified(true);

        return userRepository.save(user);
    }

    public User blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        user.setStatus(AccountStatus.BLOCKED);
        return userRepository.save(user);
    }

    public User unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        user.setStatus(AccountStatus.ACTIVE);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }

    // ===== Album Management =====

    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException("Album not found"));
        albumRepository.delete(album);
    }

    public Album updateAlbum(Album album) {
        if (!albumRepository.existsById(album.getId())) {
            throw new AlbumNotFoundException("Album not found");
        }
        return albumRepository.save(album);
    }

    public Album createAlbum(Album album, User artist) {
        album.setArtist(artist);
        return albumRepository.save(album);
    }

    // ===== Music Management =====

    public void deleteMusic(Long musicId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found"));
        musicRepository.delete(music);
    }


    public Music createMusic(Music music, User artist) {
        music.setArtist(artist);
        return musicRepository.save(music);
    }

    // ===== Playlist Management =====

    public void deletePlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found with ID: " + playlistId));
        playlistRepository.delete(playlist);
    }

    public Playlist updatePlaylist(Long playlistId, Playlist updatedPlaylist) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found with ID: " + playlistId));

        playlist.setName(updatedPlaylist.getName());
        playlist.setDescription(updatedPlaylist.getDescription());
        playlist.setTracks(updatedPlaylist.getTracks());
        return playlistRepository.save(playlist);
    }


    public Playlist createAdminPlaylist(Playlist playlist, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("Admin not found with ID: " + adminId));

        if (admin.getRole() != Role.ADMIN) {
            throw new UserNotFoundException("Only admin can create admin playlists");
        }

        playlist.setOwner(admin);
        return playlistRepository.save(playlist);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

}
