package com.api.videostreaming.pojos.responses;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchVideoResponse {
    private Long videoId;
    private String title;
    private String director;
    private String genre;
    private List<String> cast;
    private String message;
}
