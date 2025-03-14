package com.api.videostreaming.pojos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishVideoResponse {
    private Long videoId;
    private String title;
    private String message;
}

