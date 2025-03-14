package com.api.videostreaming.pojos.responses;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;
}