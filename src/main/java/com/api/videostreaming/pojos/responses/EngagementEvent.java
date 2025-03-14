package com.api.videostreaming.pojos.responses;

import com.api.videostreaming.enums.EngagementType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngagementEvent {
    private Long videoId;
    private Long userId;
    private EngagementType type; // IMPRESSION or VIEW
}

