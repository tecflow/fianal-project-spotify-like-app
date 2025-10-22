package com.spotify_final_project;

import com.spotify_final_project.model.Listen;
import com.spotify_final_project.model.Music;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.ListenRepository;
import com.spotify_final_project.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    private ListenRepository listenRepository;
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        listenRepository = mock(ListenRepository.class);
        statisticsService = new StatisticsService(listenRepository);
    }

    @Test
    void printWeeklyStatistics_ShouldCallRepository() {
        User user = new User();
        user.setUsername("john");

        Music music = new Music();
        music.setTitle("Song 1");

        Listen listen = new Listen();
        listen.setListener(user);
        listen.setMusic(music);
        listen.setCount(3L);

        when(listenRepository.findAll()).thenReturn(List.of(listen));

        statisticsService.printWeeklyStatistics();
        verify(listenRepository, times(1)).findAll();
    }

    @Test
    void printStatisticsForTesting_ShouldCallRepository() {
        User user = new User();
        user.setUsername("alice");

        Music music = new Music();
        music.setTitle("Song 2");

        Listen listen = new Listen();
        listen.setListener(user);
        listen.setMusic(music);
        listen.setCount(5L);

        when(listenRepository.findAll()).thenReturn(List.of(listen));

        statisticsService.printStatisticsForTesting();
        verify(listenRepository, times(1)).findAll();
    }
}
