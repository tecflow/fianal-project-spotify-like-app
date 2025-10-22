package com.spotify_final_project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spotify_final_project.enums.GenreType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private GenreType genre; // Added genre

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist; // Album owner (artist)

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Music> tracks = new ArrayList<>(); // Added musics
}
