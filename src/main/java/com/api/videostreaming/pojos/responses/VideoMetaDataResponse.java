package com.api.videostreaming.pojos.responses;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoMetaDataResponse {
    private Long videoId;
    private String title;
    private String director;
    private String genre;
    private Integer releaseYear;
    private Integer runningTime;
}
