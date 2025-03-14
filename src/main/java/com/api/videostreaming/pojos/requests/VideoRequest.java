package com.api.videostreaming.pojos.requests;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRequest {
    
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Director name is required")
    private String director;

    @NotNull(message = "Cast list cannot be null")
    private List<String> cast;

    @NotBlank(message = "File URL is required")
    private String fileUrl;  // URL to the video file

    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be greater than 0")
    private Long fileSize;  // File size in bytes

    @NotBlank(message = "Format is required")
    private String format;  // e.g., mp4, mkv, avi

    @NotNull(message = "Resolution is required")
    private Integer resolution;  // e.g., 1080 for 1080p

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer duration;  // Duration in seconds

    @Valid
    @NotNull(message = "Metadata is required")
    private MetadataRequest metadata;
}
