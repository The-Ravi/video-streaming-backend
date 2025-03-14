package com.api.videostreaming.implsTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.api.videostreaming.entities.*;
import com.api.videostreaming.exceptions.*;
import com.api.videostreaming.exceptions.customExceptions.InternalServerErrorException;
import com.api.videostreaming.exceptions.customExceptions.ResourceNotFoundException;
import com.api.videostreaming.pojos.requests.*;
import com.api.videostreaming.pojos.responses.*;
import com.api.videostreaming.repositories.VideoRepository;
import com.api.videostreaming.serviceImpls.VideoServiceImpl;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTests {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoServiceImpl videoService;

    private Video video;
    private VideoRequest videoRequest;
    private MetadataRequest metadataRequest;

    @BeforeEach
    void setUp() {
        metadataRequest = MetadataRequest.builder()
                .genre("Action")
                .synopsis("Great movie")
                .yearOfRelease(2024)
                .runningTime(120)
                .build();

        videoRequest = VideoRequest.builder()
                .title("Test Video")
                .director("John Doe")
                .cast(List.of("Jane Doe", "Bob Smith"))
                .fileUrl("http://example.com/video.mp4")
                .format("mp4")
                .fileSize(5000000L)
                .resolution(1080)
                .duration(3600)
                .metadata(metadataRequest)
                .build();

        video = Video.builder()
                .id(1L)
                .title(videoRequest.getTitle())
                .director(videoRequest.getDirector())
                .cast(videoRequest.getCast())
                .fileUrl(videoRequest.getFileUrl())
                .fileSize(videoRequest.getFileSize())
                .format(videoRequest.getFormat())
                .resolution(videoRequest.getResolution())
                .duration(videoRequest.getDuration())
                .isActive(true)
                .metadata(VideoMetadata.builder()
                        .video(video)
                        .synopsis(metadataRequest.getSynopsis())
                        .yearOfRelease(metadataRequest.getYearOfRelease())
                        .genre(metadataRequest.getGenre())
                        .runningTime(metadataRequest.getRunningTime())
                        .build())
                .build();
    }

    @Test
    void shouldPublishVideoSuccessfully() {
        when(videoRepository.existsByTitleIgnoreCase(videoRequest.getTitle())).thenReturn(false);
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        ResponseEntity<PublishVideoResponse> response = videoService.publishVideo(videoRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Video successfully published", response.getBody().getMessage());

        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    void shouldReturnConflictWhenVideoAlreadyExists() {
        when(videoRepository.existsByTitleIgnoreCase(videoRequest.getTitle())).thenReturn(true);

        ResponseEntity<PublishVideoResponse> response = videoService.publishVideo(videoRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void shouldThrowExceptionWhenPublishingFails() {
        when(videoRepository.save(any(Video.class))).thenThrow(new RuntimeException("Database Error"));

        Exception exception = assertThrows(InternalServerErrorException.class, () -> videoService.publishVideo(videoRequest));
        assertEquals("Failed to publish video: Test Video", exception.getMessage());
    }

    @Test
    void testSoftDeleteVideo_Success() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        ResponseEntity<SoftDeleteResponse> response = videoService.softDeleteVideo(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(video.isActive());
    }

    @Test
    void testSoftDeleteVideo_NotFound() {
        when(videoRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<SoftDeleteResponse> response = videoService.softDeleteVideo(2L);

        // Assert that the response status is 404 NOT FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Video not found", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());

        verify(videoRepository, times(1)).findById(2L);
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void testLoadVideoContent_Success() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        ResponseEntity<LoadVideoResponse> response = videoService.loadVideoContent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Video", response.getBody().getTitle());
    }

    @Test
    void testLoadVideoContent_NotFound() {
        when(videoRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> videoService.loadVideoContent(2L));
        assertEquals("Video not found", exception.getMessage());
    }

    @Test
    void testSearchVideos_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Video> videoPage = new PageImpl<>(List.of(video), pageable, 1);

        when(videoRepository.searchVideos("Action", pageable)).thenReturn(videoPage);

        ResponseEntity<Page<SearchVideoResponse>> response = videoService.searchVideos("Action", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testSearchVideos_NoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Video> emptyPage = Page.empty();

        when(videoRepository.searchVideos("Unknown", pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<SearchVideoResponse>> response = videoService.searchVideos("Unknown", 0, 10);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testPlayVideoContent_Success() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        ResponseEntity<PlayVideoResponse> response = videoService.playVideoContent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://example.com/video.mp4", response.getBody().getFileUrl());
    }

    @Test
    void testPlayVideoContent_FileUrlMissing() {
        video.setFileUrl(null);
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class, () -> 
            videoService.playVideoContent(1L)
        );

        assertEquals("Video file URL is missing", exception.getMessage());

        verify(videoRepository, times(1)).findById(1L);
    }
}
