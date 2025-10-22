package com.spotify_final_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class  SocialPlatformApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialPlatformApiApplication.class, args);
    }

}
