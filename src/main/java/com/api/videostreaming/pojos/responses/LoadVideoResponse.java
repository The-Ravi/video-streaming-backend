package com.api.videostreaming.pojos.responses;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoadVideoResponse {
   
    private Long videoId;
    private String title;
    private String director;
    private List<String> cast;
    private String fileUrl;
    private Long fileSize;
    private String format;
    private Integer resolution;
    private Integer duration;
    private String message;
    private boolean success;
}
