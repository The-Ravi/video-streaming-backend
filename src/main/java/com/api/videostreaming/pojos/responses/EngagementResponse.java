package com.api.videostreaming.pojos.responses;

import com.api.videostreaming.enums.EngagementType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngagementResponse {
    private Long videoId;
    private Long userId;
    private String title;
    private int impressions;
    private int views;
    private String message;
    private EngagementType type;
    private boolean success;
}
