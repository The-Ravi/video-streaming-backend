package com.api.videostreaming.services;

import org.springframework.http.ResponseEntity;

import com.api.videostreaming.enums.EngagementType;
import com.api.videostreaming.pojos.responses.EngagementResponse;

public interface EngagementService {
    ResponseEntity<EngagementResponse> trackEngagement(Long videoId, Long userId, EngagementType type);
    ResponseEntity<EngagementResponse> getEngagements(Long videoId);
}