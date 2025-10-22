package com.spotify_final_project.service;

import com.spotify_final_project.model.Listen;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.ListenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListenService {

    private final ListenRepository listenRepository;

    @Transactional
    public void incrementListenCount(User listener, Music music) {
        Listen listen = listenRepository.findByListenerAndMusic(listener, music)
                .orElseGet(() -> new Listen()
                        .setListener(listener)
                        .setMusic(music)
                        .setCount(0L));

        listen.setCount(listen.getCount() + 1);
        listenRepository.save(listen);
    }
}
