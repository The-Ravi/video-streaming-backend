package com.api.videostreaming.pojos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String message;
}
