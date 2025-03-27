package com.api.videostreaming.implsTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private Video video;
    private VideoEngagements engagement;
    private final Long videoId = 1L;

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
                .impressions(5)
                .views(2)
                .build();
    }

    /**
     * Test: Engagement event is sent to Kafka when `useKafka = true`
     */
    @Test
    void testTrackEngagement_WithKafka() {
        ReflectionTestUtils.setField(engagementService, "useKafka", true);
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));

        ResponseEntity<EngagementResponse> response = engagementService.trackEngagement(videoId, EngagementType.VIEW);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engagement event sent to Kafka", response.getBody().getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, never()).findByVideoId(any());
        verify(engagementRepository, never()).save(any());
    }

    /**
     * Test: Engagement is stored in DB when `useKafka = false` (New Engagement)
     */
    @Test
    void testTrackEngagement_WithoutKafka_NewEngagement() {
        ReflectionTestUtils.setField(engagementService, "useKafka", false);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(engagementRepository.findByVideoId(videoId)).thenReturn(Optional.empty());
        when(engagementRepository.save(any(VideoEngagements.class))).thenReturn(engagement);

        ResponseEntity<EngagementResponse> response = engagementService.trackEngagement(videoId, EngagementType.IMPRESSION);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engagement recorded successfully", response.getBody().getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, times(1)).findByVideoId(videoId);
        verify(engagementRepository, times(1)).save(any(VideoEngagements.class));
    }

    /**
     * Test: Engagement is updated when it already exists
     */
    @Test
    void testTrackEngagement_UpdateExistingEngagement() {
        ReflectionTestUtils.setField(engagementService, "useKafka", false);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(engagementRepository.findByVideoId(videoId)).thenReturn(Optional.of(engagement));
        when(engagementRepository.save(any(VideoEngagements.class))).thenReturn(engagement);

        ResponseEntity<EngagementResponse> response = engagementService.trackEngagement(videoId, EngagementType.VIEW);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engagement recorded successfully", response.getBody().getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, times(1)).findByVideoId(videoId);
        verify(engagementRepository, times(1)).save(any(VideoEngagements.class));

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
                () -> engagementService.trackEngagement(videoId, EngagementType.VIEW));

        assertEquals("Video not found for ID: " + videoId, exception.getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, never()).findByVideoId(any());
        verify(engagementRepository, never()).save(any());
    }

    /**
     * Test: Successfully retrieves engagement statistics
     */
    @Test
    void testGetEngagements_Success() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(engagementRepository.findByVideoId(videoId)).thenReturn(Optional.of(engagement));

        ResponseEntity<EngagementResponse> response = engagementService.getEngagements(videoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Engagement statistics retrieved successfully", response.getBody().getMessage());
        assertEquals(5, response.getBody().getImpressions());
        assertEquals(2, response.getBody().getViews());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, times(1)).findByVideoId(videoId);
    }

    /**
     * Test: Throws exception when video does not exist in DB
     */
    @Test
    void testGetEngagements_VideoNotFound() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> engagementService.getEngagements(videoId));

        assertEquals("Video not found for ID: " + videoId, exception.getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, never()).findByVideoId(any());
    }

    /**
     * Test: Throws exception when engagement data is missing
     */
    @Test
    void testGetEngagements_EngagementNotFound() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(engagementRepository.findByVideoId(videoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> engagementService.getEngagements(videoId));

        assertEquals("Engagement data not found for Video ID: " + videoId, exception.getMessage());

        verify(videoRepository, times(1)).findById(videoId);
        verify(engagementRepository, times(1)).findByVideoId(videoId);
    }
}
