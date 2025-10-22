package com.spotify_final_project.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spotify_final_project.enums.GenreType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "music")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenreType genre;

    private int duration; // დავრის დრო წამებში

    @ManyToOne
    @JsonIgnoreProperties("tracks")
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist; // არტისტი რომელმაც ატვირთა ეს სიმღერა
}
