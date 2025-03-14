package com.api.videostreaming.pojos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataRequest {

    @NotBlank(message = "Synopsis is required")
    private String synopsis;

    @NotNull(message = "Year of release is required")
    @Min(value = 1888, message = "Year of release must be a valid year") // First movie was in 1888
    private Integer yearOfRelease;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Running time is required")
    @Min(value = 1, message = "Running time must be at least 1 minute")
    private Integer runningTime;
}
