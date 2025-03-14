package com.api.videostreaming.repositories;


import com.api.videostreaming.entities.Video;
import com.api.videostreaming.entities.VideoEngagements;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoEngagementRepository extends JpaRepository<VideoEngagements, Long> {
    Optional<VideoEngagements> findByVideoAndUserId(Video video, Long userId);
    Optional<VideoEngagements> findByVideoId(Long videoId);
    
}

