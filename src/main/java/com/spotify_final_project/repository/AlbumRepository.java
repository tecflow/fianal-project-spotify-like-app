package com.spotify_final_project.repository;

import com.spotify_final_project.model.Album;
import com.spotify_final_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtist(User artist);

    void deleteAllByArtist(User user);
}
