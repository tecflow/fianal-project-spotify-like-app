package com.spotify_final_project.repository;

import com.spotify_final_project.enums.GenreType;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findByArtist(User artist);

    List<Music> findByGenre(GenreType genre);

    void deleteAllByArtist(User user);

    List<Music> findAllByArtist(User user);

    // Search by title (case-insensitive, contains)
    List<Music> findByTitleContainingIgnoreCase(String title);

    // Search by author/artist username
    List<Music> findByArtist_UsernameContainingIgnoreCase(String username);

    // Optional: combine title or artist
    List<Music> findByTitleContainingIgnoreCaseOrArtist_UsernameContainingIgnoreCase(String title, String username);


    List<Music> findAllByGenre(GenreType genre, Pageable pageable);
}
