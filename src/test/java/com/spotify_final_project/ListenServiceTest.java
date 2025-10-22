package com.spotify_final_project;

import com.spotify_final_project.model.Listen;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.ListenRepository;
import com.spotify_final_project.service.ListenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ListenServiceTest {

    private ListenRepository listenRepository;
    private ListenService listenService;

    @BeforeEach
    void setUp() {
        listenRepository = mock(ListenRepository.class);
        listenService = new ListenService(listenRepository);
    }

    @Test
    void incrementListenCount_ShouldCreateNewListen_WhenNoExistingRecord() {
        User listener = new User();
        Music music = new Music();

        when(listenRepository.findByListenerAndMusic(listener, music))
                .thenReturn(Optional.empty());

        listenService.incrementListenCount(listener, music);

        verify(listenRepository, times(1)).save(argThat(listen ->
                listen.getListener() == listener &&
                        listen.getMusic() == music &&
                        listen.getCount() == 1L
        ));
    }

    @Test
    void incrementListenCount_ShouldIncrementExistingListen() {
        User listener = new User();
        Music music = new Music();
        Listen existingListen = new Listen().setListener(listener).setMusic(music).setCount(5L);

        when(listenRepository.findByListenerAndMusic(listener, music))
                .thenReturn(Optional.of(existingListen));

        listenService.incrementListenCount(listener, music);

        assertEquals(6L, existingListen.getCount());
        verify(listenRepository, times(1)).save(existingListen);
    }
}
