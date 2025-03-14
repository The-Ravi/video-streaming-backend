package com.api.videostreaming.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String director;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "video_cast", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "cast_member")
    private List<String> cast;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;

    // Video content fields (Merged from VideoContent)
    @Column(nullable = false)
    private String fileUrl;  // URL to the video file (AWS S3, CDN, etc.)

    @Column(nullable = false)
    private Long fileSize;  // File size in bytes

    private String format;   // e.g., mp4, mkv, avi

    private Integer resolution;  // e.g., 1080 for 1080p

    private Integer duration;  // Duration in seconds

    @JsonIgnore
    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VideoMetadata metadata;
}
