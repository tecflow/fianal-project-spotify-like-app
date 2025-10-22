package com.spotify_final_project.repository;

import com.spotify_final_project.model.Playlist;
import com.spotify_final_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwner(User owner);

    void deleteAllByOwner(User user);


    @Modifying
    @Query(value = "DELETE FROM playlist_music WHERE music_id = :musicId", nativeQuery = true)
    void removeMusicFromAllPlaylists(@Param("musicId") Long musicId);
}
