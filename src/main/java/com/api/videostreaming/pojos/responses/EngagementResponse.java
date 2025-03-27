package com.api.videostreaming.pojos.responses;

import com.api.videostreaming.enums.EngagementType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) 
@Builder
public class EngagementResponse {
    private Long videoId;
    private Long userId;
    private String title;
    private Integer impressions;
    private Integer views;
    private String message;
    private EngagementType type;
    private boolean success;
}
