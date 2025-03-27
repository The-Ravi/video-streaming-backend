package com.api.videostreaming.controllers;

import com.api.videostreaming.enums.EngagementType;
import com.api.videostreaming.pojos.responses.EngagementResponse;
import com.api.videostreaming.services.EngagementService;
import com.api.videostreaming.utilities.URIConstants;

import ch.qos.logback.classic.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URIConstants.API_VERSION + URIConstants.ES_BASE_URL)
@RequiredArgsConstructor
public class EngagementController {
    private static final Logger log = (Logger) LoggerFactory.getLogger(EngagementController.class);
    private final EngagementService engagementTrackingService;

    @Operation(
            summary = "Track engagement for a video",
            description = "Records impressions or views for a video based on user actions.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @PostMapping(URIConstants.TRACK_ENGAGEMENT)
    public ResponseEntity<EngagementResponse> trackEngagement(
            @PathVariable Long videoId,
            @RequestParam EngagementType type) {

        log.info("Tracking engagement: Video ID={}, User ID={}, Type={}", videoId, type);
        ResponseEntity<EngagementResponse> response = engagementTrackingService.trackEngagement(videoId, type);
        log.info("Engagement tracked successfully. Returning response: {}", response);
        return response;
    }

    @Operation(
            summary = "Get engagement stats for a video",
            description = "Retrieves engagement statistics including impressions and views for a given video.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping(URIConstants.VIDEO_ID)
    public ResponseEntity<EngagementResponse> getEngagement(@PathVariable Long videoId) {

        log.info("Fetching engagement stats for Video ID={}", videoId);
        ResponseEntity<EngagementResponse> response = engagementTrackingService.getEngagements(videoId);
        log.info("Returning engagement stats: {}", response.getBody());
        return response;
    }
}
