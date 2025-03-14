package com.api.videostreaming.services;

import org.springframework.http.ResponseEntity;

import com.api.videostreaming.pojos.requests.LoginRequest;
import com.api.videostreaming.pojos.requests.RefreshTokenRequest;
import com.api.videostreaming.pojos.responses.JwtResponse;

public interface AuthService {
        ResponseEntity<JwtResponse> generateToken(LoginRequest loginReq);
        ResponseEntity<JwtResponse> refreshToken(RefreshTokenRequest refreshTokenReq);

}
