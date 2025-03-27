package com.api.videostreaming.serviceImpls;

import com.api.videostreaming.entities.Video;
import com.api.videostreaming.entities.VideoEngagements;
import com.api.videostreaming.enums.EngagementType;
import com.api.videostreaming.exceptions.customExceptions.ResourceNotFoundException;
import com.api.videostreaming.pojos.responses.EngagementResponse;
import com.api.videostreaming.repositories.VideoEngagementRepository;
import com.api.videostreaming.repositories.VideoRepository;
import com.api.videostreaming.services.EngagementService;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EngagementServiceImpl implements EngagementService {
    private static final Logger log = (Logger) LoggerFactory.getLogger(EngagementServiceImpl.class);
    private final VideoRepository videoRepository;
    private final VideoEngagementRepository engagementRepository;

    @Value("${engagement.useKafka}") 
    private boolean useKafka;


    @Override
    @Transactional
    public ResponseEntity<EngagementResponse> trackEngagement(Long videoId, EngagementType type) {
        log.info("Processing engagement tracking for Video ID={}, Type={}", videoId, type);
    
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found for ID: " + videoId));
    
        if (useKafka) {
            // Simulate sending event to Kafka
            // KafkaTemplate<String, String> kafkaTemplate
            // kafkaTemplate.send(topic, message)
            log.info("Engagement event sent to Kafka for Video ID: {}, Type: {}", videoId, type);
    
            EngagementResponse response = EngagementResponse.builder()
                    .success(true)
                    .message("Engagement event sent to Kafka")
                    .videoId(videoId)
                    .type(type)
                    .build();
    
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } else {
            // Fetch existing engagement or create a new one
            VideoEngagements engagement = engagementRepository.findByVideoId(videoId).orElse(null);
    
            if (engagement == null) {
                engagement = VideoEngagements.builder()
                        .video(video)
                        .build();
                log.info("New engagement created for Video ID={}", videoId);
            } else {
                if (type == EngagementType.IMPRESSION) {
                    engagement.setImpressions(engagement.getImpressions() + 1);
                } else {
                    engagement.setViews(engagement.getViews() + 1);
                }
                log.info("Updated engagement for Video ID={} -> Impressions={}, Views={}",
                        videoId, engagement.getImpressions(), engagement.getViews());
            }
    
            engagementRepository.save(engagement);
            log.info("Engagement recorded in DB for Video ID={}, Type={}", videoId, type);
    
            EngagementResponse response = EngagementResponse.builder()
                    .success(true)
                    .message("Engagement recorded successfully")
                    .videoId(videoId)
                    .build();
    
            log.info("Engagement response : {}",response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
    

    @Override
    public ResponseEntity<EngagementResponse> getEngagements(Long videoId) {
        log.info("Fetching engagement stats for Video ID={}", videoId);
    
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found for ID: " + videoId));
    
        // Fetch impressions & views directly from the database
        VideoEngagements engagement = engagementRepository.findByVideoId(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement data not found for Video ID: " + videoId));
    
        EngagementResponse response = EngagementResponse.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .impressions(engagement.getImpressions()) // Fetch actual impression count
                .views(engagement.getViews()) // Fetch actual view count
                .message("Engagement statistics retrieved successfully")
                .success(true)
                .build();
    
        log.info("Returning engagement stats for Video ID={} -> Impressions={}, Views={}",
                videoId, engagement.getImpressions(), engagement.getViews());
    
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }    
}
