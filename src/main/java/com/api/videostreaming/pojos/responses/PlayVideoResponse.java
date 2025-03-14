package com.api.videostreaming.pojos.responses;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayVideoResponse {
    private Long videoId;
    private String title;
    private String fileUrl;
    private String format;
    private Integer resolution;
    private Integer duration;
    private String message;
    private boolean success;
}

