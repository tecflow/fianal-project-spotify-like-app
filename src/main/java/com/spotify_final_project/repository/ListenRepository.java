package com.spotify_final_project.repository;

import com.spotify_final_project.model.Listen;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListenRepository extends JpaRepository<Listen, Long> {
    Optional<Listen> findByListenerAndMusic(User listener, Music music);
    List<Listen> findAllByListener(User listener);

    List<Listen> findByListener(User user);
}
