package com.api.videostreaming.pojos.responses;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataResponse {
    private Long videoId;
    private String message;
    private boolean success;
}
