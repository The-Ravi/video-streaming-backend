package com.api.videostreaming.serviceImpls;

import com.api.videostreaming.entities.Users;
import com.api.videostreaming.exceptions.customExceptions.AuthenticationFailedException;
import com.api.videostreaming.exceptions.customExceptions.ForbiddenException;
import com.api.videostreaming.exceptions.customExceptions.ResourceNotFoundException;
import com.api.videostreaming.pojos.requests.LoginRequest;
import com.api.videostreaming.pojos.requests.RefreshTokenRequest;
import com.api.videostreaming.pojos.responses.JwtResponse;
import com.api.videostreaming.repositories.UsersRepository;
import com.api.videostreaming.securities.JwtUtil;
import com.api.videostreaming.services.AuthService;
import com.api.videostreaming.utilities.Constants;
import com.google.gson.Gson;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = (Logger) LoggerFactory.getLogger(AuthServiceImpl.class);
    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepo; 
    private final Gson gson = new Gson();

    @Override
    public ResponseEntity<JwtResponse> generateToken(LoginRequest loginReq) {
        String userName = loginReq.getUsername();
        String password = loginReq.getPassword();

        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            log.warn("generateToken: userName/password is null/empty");
            throw new AuthenticationFailedException(Constants.INVALID_PARAMETERS);
        }

        Users user = usersRepo.findByUserName(userName);
        if (user == null) {
            log.warn("generateToken: User not found");
            throw new AuthenticationFailedException(Constants.BAD_CREDENTIALS);
        }

        // Ensure password is encoded before checking
        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        if (!encodedPassword.equals(user.getPassword())) {
            log.warn("generateToken: Invalid credentials");
            throw new AuthenticationFailedException(Constants.BAD_CREDENTIALS);
        }

        log.debug("generateToken: userData: {}", gson.toJson(user));

        String jwtToken = jwtUtil.generateToken(userName, Constants.TYPE_AUTH_TOKEN);
        String refreshToken = jwtUtil.generateToken(userName, Constants.TYPE_REFRESH_TOKEN);

        // Use Builder for Response
        JwtResponse response = JwtResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .message(Constants.TOKEN_GENERATED)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<JwtResponse> refreshToken(RefreshTokenRequest refreshTokenReq) {
        String refreshToken = refreshTokenReq.getRefreshToken();

        log.info("Processing refresh token request.");

        // Validate Refresh Token is not empty
        if (StringUtils.isBlank(refreshToken)) {
            log.warn("Refresh token is null or empty.");
            throw new ResourceNotFoundException(Constants.INVALID_PARAMETERS);
        }

        // Validate Token Type
        String tokenType = jwtUtil.getCustomClaimFromToken(refreshToken, Constants.TOKEN_TYPE);
        if (tokenType == null || !tokenType.equals(Constants.TYPE_REFRESH_TOKEN)) {
            log.warn("Invalid refresh token provided.");
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        // Check if Token is Expired
        if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(refreshToken))) {
            log.warn("Refresh token has expired.");
            throw new ForbiddenException(Constants.JWT_TOKEN_EXPIRED);
        }

        // Extract Username from Token
        String userName = jwtUtil.getUsernameFromToken(refreshToken);
        log.info("Generating new tokens for user: {}", userName);

        // Generate New Access & Refresh Tokens
        String newJwtToken = jwtUtil.generateToken(userName, Constants.TYPE_AUTH_TOKEN);
        String newRefreshToken = jwtUtil.generateToken(userName, Constants.TYPE_REFRESH_TOKEN);

        JwtResponse response = JwtResponse.builder()
                .token(newJwtToken)
                .refreshToken(newRefreshToken)
                .message(Constants.TOKEN_GENERATED)
                .build();

        log.info("New access token & refresh token generated successfully for user: {}", userName);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
