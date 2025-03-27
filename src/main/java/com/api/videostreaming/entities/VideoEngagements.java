package com.api.videostreaming.entities;

import com.api.videostreaming.enums.EngagementType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "video_engagements")
public class VideoEngagements {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Enumerated(EnumType.STRING)
    private EngagementType type;

    private int impressions;
    private int views;
}
