package com.api.videostreaming.serviceImpls;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.videostreaming.entities.Video;
import com.api.videostreaming.entities.VideoMetadata;
import com.api.videostreaming.exceptions.customExceptions.InternalServerErrorException;
import com.api.videostreaming.exceptions.customExceptions.ResourceNotFoundException;
import com.api.videostreaming.pojos.requests.MetadataRequest;
import com.api.videostreaming.pojos.requests.VideoRequest;
import com.api.videostreaming.pojos.responses.LoadVideoResponse;
import com.api.videostreaming.pojos.responses.MetadataResponse;
import com.api.videostreaming.pojos.responses.PlayVideoResponse;
import com.api.videostreaming.pojos.responses.PublishVideoResponse;
import com.api.videostreaming.pojos.responses.SearchVideoResponse;
import com.api.videostreaming.pojos.responses.SoftDeleteResponse;
import com.api.videostreaming.pojos.responses.VideoMetaDataResponse;
import com.api.videostreaming.repositories.VideoRepository;
import com.api.videostreaming.services.VideoService;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private static final Logger log = (Logger) LoggerFactory.getLogger(VideoService.class);
    private final VideoRepository videoRepository;

    @Override
    public ResponseEntity<PublishVideoResponse> publishVideo(VideoRequest request) {
        log.info("Publishing new video: {}", request.getTitle());

        // Check if video already exists
        if (videoRepository.existsByTitleIgnoreCase(request.getTitle())) {
            log.warn("Video with title '{}' already exists", request.getTitle());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new PublishVideoResponse(null, request.getTitle(), "Video already exists"));
        }

        try {
            Video video = Video.builder()
                    .title(request.getTitle())
                    .director(request.getDirector())
                    .cast(request.getCast())
                    .fileUrl(request.getFileUrl())  // Ensure this field is populated
                    .fileSize(request.getFileSize())  // Ensure this field is not null
                    .format(request.getFormat())
                    .resolution(request.getResolution())
                    .duration(request.getDuration())
                    .isActive(true)
                    .build();

            // Set metadata directly inside Video
            VideoMetadata metadata = VideoMetadata.builder()
                    .video(video)
                    .synopsis(request.getMetadata().getSynopsis())
                    .yearOfRelease(request.getMetadata().getYearOfRelease())
                    .genre(request.getMetadata().getGenre())
                    .runningTime(request.getMetadata().getRunningTime())
                    .build();

            video.setMetadata(metadata); // Auto-save metadata due to @OneToOne(cascade = CascadeType.ALL)

            video = videoRepository.save(video);

            log.info("Video '{}' published successfully with ID: {}", video.getTitle(), video.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new PublishVideoResponse(video.getId(), video.getTitle(), "Video successfully published"));

        } catch (Exception e) {
            log.error("Error occurred while publishing video: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to publish video: " + request.getTitle());

        }
    }

    @Override
    public ResponseEntity<MetadataResponse> addOrEditVideoMetadata(Long videoId, MetadataRequest request) {
        log.info("Updating metadata for video ID: {}", videoId);
    
        // Fetch the existing video
        Video video = videoRepository.findById(videoId)
                .orElse(null);
    
        if (video == null) {
            log.warn("Video with ID '{}' not found", videoId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MetadataResponse(videoId, "Video not found", false));
        }
    
        try {
            // Get existing metadata or create new one
            VideoMetadata metadata = video.getMetadata();
            if (metadata == null) {
                metadata = new VideoMetadata();
                metadata.setVideo(video);
            }
    
            // Update fields only if request values are not null
            if (request.getSynopsis() != null) {
                metadata.setSynopsis(request.getSynopsis());
            }
            if (request.getYearOfRelease() != null) {
                metadata.setYearOfRelease(request.getYearOfRelease());
            }
            if (request.getGenre() != null) {
                metadata.setGenre(request.getGenre());
            }
            if (request.getRunningTime() != null) {
                metadata.setRunningTime(request.getRunningTime());
            }
    
            video.setMetadata(metadata);
            videoRepository.save(video);
    
            log.info("Metadata updated successfully for video ID: {}", videoId);
    
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new MetadataResponse(videoId, "Metadata updated successfully", true));
    
        } catch (Exception e) {
            log.error("Error updating metadata for video ID: {}", videoId, e);
            throw new InternalServerErrorException("Failed to update metadata for video ID: " + videoId);
        }
    }    


    @Override
    public ResponseEntity<SoftDeleteResponse> softDeleteVideo(Long videoId) {
        log.info("Attempting to soft delete video ID: {}", videoId);

        // Fetch the existing video
        Video video = videoRepository.findById(videoId)
                .orElse(null);

        if (video == null) {
            log.warn("Video with ID '{}' not found", videoId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SoftDeleteResponse(videoId, "Video not found", false));
        }

        if (!video.isActive()) {
            log.warn("Video with ID '{}' is already soft deleted", videoId);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SoftDeleteResponse(videoId, "Video is already soft deleted", false));
        }

        try {
            // Perform soft delete
            video.setActive(false);
            videoRepository.save(video);

            log.info("Video ID '{}' has been soft deleted", videoId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SoftDeleteResponse(videoId, "Video soft deleted successfully", true));

        } catch (Exception e) {
            log.error("Error occurred while soft deleting video ID: {}", videoId, e);
            throw new InternalServerErrorException("Failed to soft delete video");
        }
    }


    @Override
    public ResponseEntity<LoadVideoResponse> loadVideoContent(Long videoId) {
        log.info("Fetching video content for Video ID: {}", videoId);
    
        // Fetch video directly from VideoRepository
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));
    
        // Check if file URL is missing (Internal Server Error case)
        if (video.getFileUrl() == null || video.getFileUrl().isEmpty()) {
            log.error("Internal Server Error: Video file URL is missing for Video ID: {}", videoId);
            throw new InternalServerErrorException("Video file URL is missing");
        }
    
        // Build response with metadata and video file details
        LoadVideoResponse response = LoadVideoResponse.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .director(video.getDirector())
                .cast(video.getCast())
                .fileUrl(video.getFileUrl())
                .fileSize(video.getFileSize())
                .format(video.getFormat())
                .resolution(video.getResolution())
                .duration(video.getDuration())
                .message("Video content loaded successfully")
                .success(true)
                .build();
    
        log.info("Video content loaded successfully: ID={}, Title={}", videoId, video.getTitle());
    
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @Override
    public ResponseEntity<PlayVideoResponse> playVideoContent(Long videoId) {
        log.info("Fetching video content for ID: {}", videoId);

        // Fetch the video directly from VideoRepository
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));

        // Check if video has a valid file URL
        if (video.getFileUrl() == null || video.getFileUrl().isEmpty()) {
            log.error("Internal Server Error: Video file URL is missing for Video ID: {}", videoId);
            throw new InternalServerErrorException("Video file URL is missing");
        }

        // Build response with video streaming details
        PlayVideoResponse response = PlayVideoResponse.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .fileUrl(video.getFileUrl())
                .format(video.getFormat())
                .resolution(video.getResolution())
                .duration(video.getDuration())
                .message("Video is ready to play")
                .success(true)
                .build();

        log.info("Video content ready to play: ID={}, Title={}", videoId, video.getTitle());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


     @Override
    public ResponseEntity<Page<SearchVideoResponse>> searchVideos(String searchPhrase, int page, int size) {
        log.info("Searching videos with phrase: '{}', page={}, size={}", searchPhrase, page, size);

        Pageable pageable = PageRequest.of(page, size);

        // Search across metadata fields (title, director, genre, cast)
        Page<Video> videoPage = videoRepository.searchVideos(searchPhrase, pageable);

        if (videoPage.isEmpty()) {
            log.warn("No videos found for search phrase: '{}'", searchPhrase);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Page.empty());
        }

        // Convert Video entities to SearchVideoResponse DTO
        Page<SearchVideoResponse> responsePage = videoPage.map(video -> SearchVideoResponse.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .director(video.getDirector())
                .genre(video.getMetadata().getGenre())
                .cast(video.getCast())
                .message("Search successful")
                .build());

        log.info("Search completed: Found {} videos", videoPage.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(responsePage);
    }

    @Override
    public ResponseEntity<Page<VideoMetaDataResponse>> getAllVideos(int page, int size) {
        log.info("Fetching all videos: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated videos
        Page<Video> videoPage = videoRepository.findAll(pageable);
        if (videoPage.isEmpty()) {
            log.warn("No videos found in the database");
             throw new ResourceNotFoundException("No videos found");
        }

        // Convert Video entities to VideoMetaDataResponse DTO
        Page<VideoMetaDataResponse> responsePage = videoPage.map(video -> VideoMetaDataResponse.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .director(video.getDirector())
                .genre(video.getMetadata().getGenre())
                .releaseYear(video.getMetadata().getYearOfRelease())
                .runningTime(video.getMetadata().getRunningTime())
                .build());

        log.info("Fetched {} videos successfully", videoPage.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(responsePage);
    }

    
}
