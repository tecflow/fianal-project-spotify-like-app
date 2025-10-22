package com.spotify_final_project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "listens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "music_id"})
})
@Getter
@Setter
@Accessors(chain = true)
public class Listen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User listener;

    @ManyToOne
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    private Long count = 0L; // Number of times this user listened to this song
}