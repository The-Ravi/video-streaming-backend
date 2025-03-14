package com.api.videostreaming.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "video_metadata")
public class VideoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "video_id", nullable = false, unique = true)
    private Video video;

    private String synopsis;
    private int yearOfRelease;
    private String genre;
    private int runningTime;
}
