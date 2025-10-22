package com.spotify_final_project.service;

import com.spotify_final_project.model.Listen;
import com.spotify_final_project.repository.ListenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ListenRepository listenRepository;

    @Scheduled(cron = "0 0 0 ? * FRI")
    public void printWeeklyStatistics() {
        List<Listen> listens = listenRepository.findAll();

        System.out.println("==== Weekly Music Listen Statistics ====");
        for (Listen listen : listens) {
            System.out.printf("User: %s, Song: %s, Count: %d%n",
                    listen.getListener().getUsername(),
                    listen.getMusic().getTitle(),
                    listen.getCount());
        }
        System.out.println("=======================================");
    }

    @Scheduled(cron = "0 * * * * *")
    public void printStatisticsForTesting() {
        List<Listen> listens = listenRepository.findAll();
        System.out.println("==== Test Listen Statistics ====");
        for (Listen listen : listens) {
            System.out.printf("User: %s, Song: %s, Count: %d%n",
                    listen.getListener().getUsername(),
                    listen.getMusic().getTitle(),
                    listen.getCount());
        }
        System.out.println("================================");
    }
}
