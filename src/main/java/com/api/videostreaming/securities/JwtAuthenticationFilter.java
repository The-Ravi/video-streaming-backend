package com.api.videostreaming.securities;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.api.videostreaming.configs.PermittedEndpointsConfig;
import com.api.videostreaming.pojos.responses.ErrorResponse;
import com.api.videostreaming.utilities.Constants;
import com.google.gson.Gson;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final Gson gson;
    private final JwtUtil jwtUtil;
    public JwtAuthenticationFilter(Gson gson, JwtUtil jwtUtil) {
        this.gson = gson;
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        // Check if the request matches any of the permitted patterns
        RequestMatcher[] permittedMatchers = PermittedEndpointsConfig.getPermittedMatchers();
        for (RequestMatcher matcher : permittedMatchers) {
            if (matcher.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("MISSING AUTHORIZATION HEADER: authorizationHeader: {}", authorizationHeader);
            setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, Constants.UNAUTHORIZED_CODE, Constants.MISSING_AUTHORIZATION_HEADER);
            return;
        }

        jwtToken = authorizationHeader.substring(7);
        try {
            username = jwtUtil.getUsernameFromToken(jwtToken);
        } catch (ExpiredJwtException e) {
            setResponse(response, HttpServletResponse.SC_FORBIDDEN, Constants.EXPIRED_JWT_CODE,Constants.JWT_TOKEN_EXPIRED);
            log.warn("ACCESS TOKEN EXPIRED: token: {}, userName: {}", jwtToken, username);
        } catch (Exception e) {
            setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, Constants.UNAUTHORIZED_CODE,Constants.INVALID_JWT_TOKEN);
            log.warn("INVALID TOKEN: token: {}, userName: {}", jwtToken, username);
        }
        

        // validating token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = new User(username, username, new ArrayList<>());
            if (Boolean.TRUE.equals(jwtUtil.validateToken(jwtToken, username))) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, Constants.UNAUTHORIZED_CODE,Constants.INVALID_JWT_TOKEN);
                log.warn("INVALID TOKEN: token: {}, userName: {}", jwtToken, username);
            }
        }

        filterChain.doFilter(request, response);
    }

    // SET RESPONSE
    private void setResponse(HttpServletResponse response, int status, int errorCode, String message) {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse generalResponse = new ErrorResponse(errorCode, message);

        try {
            String jsonResponse = gson.toJson(generalResponse);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush(); // Make sure to flush the writer
            response.getWriter().close(); // Close the writer
        } catch (IOException e) {
            log.error("ERROR: setResponse", e);
        }
    }

    
}
