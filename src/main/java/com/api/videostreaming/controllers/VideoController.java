package com.api.videostreaming.controllers;

import ch.qos.logback.classic.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.videostreaming.pojos.requests.MetadataRequest;
import com.api.videostreaming.pojos.requests.VideoRequest;
import com.api.videostreaming.pojos.responses.LoadVideoResponse;
import com.api.videostreaming.pojos.responses.MetadataResponse;
import com.api.videostreaming.pojos.responses.PlayVideoResponse;
import com.api.videostreaming.pojos.responses.PublishVideoResponse;
import com.api.videostreaming.pojos.responses.SearchVideoResponse;
import com.api.videostreaming.pojos.responses.SoftDeleteResponse;
import com.api.videostreaming.pojos.responses.VideoMetaDataResponse;
import com.api.videostreaming.services.VideoService;
import com.api.videostreaming.utilities.Constants;
import com.api.videostreaming.utilities.URIConstants;

@RestController
@RequestMapping(URIConstants.API_VERSION + URIConstants.VIDEO_BASE_URL)
@RequiredArgsConstructor
public class VideoController {
    private static final Logger log = (Logger) LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;

    @Operation(summary = "API: to get all non-deleted videos metadata", description = "", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(URIConstants.GET_ALL)
    public ResponseEntity<List<VideoMetaDataResponse>> getAllVideos(@RequestParam Integer page, @RequestParam Integer size) {
        log.info("{} getAllVideos Request received: page={}, size={}", Constants.REQUEST, page, size);
        ResponseEntity<List<VideoMetaDataResponse>> response = videoService.getAllVideos(page, size);
        log.info("{} getAllVideos Response status: {}, Records found: {}", Constants.RESPONSE, 
        response.getStatusCode(), response.getBody());
        
        return response;
    }

    @Operation(
            summary = "API to publish video",
            description = "This endpoint allows authenticated users to publish videos.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PostMapping(URIConstants.PUBLISH_VIDEO)
    public ResponseEntity<PublishVideoResponse> publishVideo(@Valid @RequestBody VideoRequest request) {
        log.info("Received request to publish video: {}", request.getTitle());
        ResponseEntity<PublishVideoResponse> response = videoService.publishVideo(request);
        log.info("Response: Status = {}, Video ID = {}, Title = {}", response.getStatusCode(), response.getBody().getVideoId(), response.getBody().getTitle());
        return response;
    }

    @Operation(
            summary = "API to add or edit video metadata",
            description = "This endpoint allows authenticated users to add or update metadata for a video.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PutMapping(URIConstants.ADD_OR_EDIT_META_DATA)
    public ResponseEntity<MetadataResponse> addOrEditVideoMetadata(
            @PathVariable Long videoId,
            @Valid @RequestBody MetadataRequest request) {
        
        log.info("Received request to update metadata for video ID: {}", videoId);
        ResponseEntity<MetadataResponse> response = videoService.addOrEditVideoMetadata(videoId, request);
        log.info("Response: Status = {}, Video ID = {}", response.getStatusCode(), videoId);      
        return response;
    }

    @Operation(
            summary = "API to soft delete a video",
            description = "Marks a video as inactive instead of permanently deleting it.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @DeleteMapping(URIConstants.VIDEO_ID)
    public ResponseEntity<SoftDeleteResponse> softDeleteVideo(@PathVariable Long videoId) {
        
        log.info("Received request to soft delete video ID: {}", videoId);
        ResponseEntity<SoftDeleteResponse> response = videoService.softDeleteVideo(videoId);
        log.info("Response: Status = {}, Video ID = {}", response.getStatusCode(), videoId); 
        return response;
    }
    
    @Operation(summary = "Load video content by ID", description = "Fetches video metadata by video content ID",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(URIConstants.LOAD_VIDEO)
    public ResponseEntity<LoadVideoResponse> loadVideoContent(@PathVariable Long videoId) {
        log.info("{} loadVideoContent - Request received for videoContentId={}", Constants.REQUEST, videoId);
        ResponseEntity<LoadVideoResponse> response = videoService.loadVideoContent(videoId);
        log.info("{} loadVideoContent - Response status: {}", Constants.RESPONSE, response.getStatusCode());
        return response;
    }


    @Operation(
            summary = "API to play video content",
            description = "Fetches video content URL based on video ID.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping(URIConstants.PLAY_VIDEO)
    public ResponseEntity<PlayVideoResponse> playVideoContent(@PathVariable Long videoId) {
        log.info("Received request to play video for ID: {}", videoId);
        ResponseEntity<PlayVideoResponse> response = videoService.playVideoContent(videoId);
        log.info("Response: Status = {}, Video ID = {}", response.getStatusCode(), videoId);
        return response;
    }

    @Operation(
            summary = "API: to search on metadata (title, director, genre, cast)",
            description = "Search for videos using a search phrase across title, director, genre, and cast.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping(URIConstants.SEARCH)
    public ResponseEntity<List<SearchVideoResponse>> searchVideos(
            @RequestParam String searchPhrase,
            @RequestParam Integer page,
            @RequestParam Integer size) {
        
        log.info("Received search request: phrase='{}', page={}, size={}", searchPhrase, page, size);
        ResponseEntity<List<SearchVideoResponse>> response = videoService.searchVideos(searchPhrase, page, size);
        log.info("Response: Status = {}, Videos Found = {}", response.getStatusCode(), response.getBody());
        return response;
    }


}
