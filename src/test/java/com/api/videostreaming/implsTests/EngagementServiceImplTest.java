package com.api.videostreaming.implsTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.api.videostreaming.entities.Video;
import com.api.videostreaming.entities.VideoEngagements;
import com.api.videostreaming.enums.EngagementType;
import com.api.videostreaming.exceptions.customExceptions.ResourceNotFoundException;
import com.api.videostreaming.pojos.responses.EngagementResponse;
import com.api.videostreaming.repositories.VideoEngagementRepository;
import com.api.videostreaming.repositories.VideoRepository;
import com.api.videostreaming.serviceImpls.EngagementServiceImpl;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EngagementServiceImplTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoEngagementRepository engagementRepository;

    @InjectMocks
    private EngagementServiceImpl engagementService;

    @Value("${engagement.useKafka}")
    private boolean useKafka; // Mocking value

    private Video video;
    private VideoEngagements engagement;
    private final Long videoId = 1L;
    private final Long userId = 100L;

    @BeforeEach
    void setUp() {
        video = Video.builder()
                .id(videoId)
                .title("Sample Video")
                .fileUrl("http://example.com/sample.mp4")
                .isActive(true)
                .build();

        engagement = VideoEngagements.builder()
                .video(video)
                .userId(userId)
                .impressions(5)
                .views(2)
                .build();
    }

    /**
     * Test: Engagement event is sent to Kafka (when `useKafka = true`)
     */
    @Test
    void testTrackEngagement_WithKafka() {
        // Mock useKafka value in the service class
        ReflectionTestUtils.setField(engagementService, "useKafka", true);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        ResponseEntity<EngagementResponse> response = engagementService.trackEngagement(videoId, userId, EngagementType.VIEW);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engagement event sent to Kafka", response.getBody().getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, never()).findByVideoAndUserId(any(), any());
        verify(engagementRepository, never()).save(any());
    }

    /**
     * Test: Engagement is stored in DB when `useKafka = false`
     */
    @Test
    void testTrackEngagement_WithoutKafka_NewEngagement() {
        // Mock useKafka value in the service class
        ReflectionTestUtils.setField(engagementService, "useKafka", false);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(engagementRepository.findByVideoAndUserId(video, userId)).thenReturn(Optional.empty());
        when(engagementRepository.save(any(VideoEngagements.class))).thenReturn(engagement);

        ResponseEntity<EngagementResponse> response = engagementService.trackEngagement(videoId, userId, EngagementType.IMPRESSION);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engagement recorded successfully", response.getBody().getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, times(1)).findByVideoAndUserId(video, userId);
        verify(engagementRepository, times(1)).save(any(VideoEngagements.class));
    }

    /**
     * Test: Updates existing engagement when user has previous interactions
     */
    @Test
    void testTrackEngagement_UpdateExistingEngagement() {
        ReflectionTestUtils.setField(engagementService, "useKafka", false);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(engagementRepository.findByVideoAndUserId(video, userId)).thenReturn(Optional.of(engagement));
        when(engagementRepository.save(any(VideoEngagements.class))).thenReturn(engagement);

        ResponseEntity<EngagementResponse> response = engagementService.trackEngagement(videoId, userId, EngagementType.VIEW);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engagement recorded successfully", response.getBody().getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, times(1)).findByVideoAndUserId(video, userId);
        verify(engagementRepository, times(1)).save(engagement);

        // Ensure view count increased
        assertEquals(3, engagement.getViews()); // Previous views: 2 -> now 3
    }

    /**
     * Test: Throws exception when video is not found
     */
    @Test
    void testTrackEngagement_VideoNotFound() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> engagementService.trackEngagement(videoId, userId, EngagementType.VIEW));

        assertEquals("Video not found for ID: " + videoId, exception.getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, never()).findByVideoAndUserId(any(), any());
        verify(engagementRepository, never()).save(any());
    }
}
