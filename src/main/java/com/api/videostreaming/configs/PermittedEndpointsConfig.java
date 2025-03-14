package com.api.videostreaming.configs;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.api.videostreaming.utilities.URIConstants;


public class PermittedEndpointsConfig {
    
    private static final String VERSION = URIConstants.API_VERSION;

    public static final RequestMatcher[] PERMITTED_MATCHERS = new RequestMatcher[]{
        new AntPathRequestMatcher("/v3/api-docs/**"),
        new AntPathRequestMatcher("/swagger-ui/**"),
        new AntPathRequestMatcher("/swagger-resources/**"),
        new AntPathRequestMatcher("/webjars/**"),
        new AntPathRequestMatcher(VERSION + "/auth/**") // Ensure VERSION is correctly replaced
    };

    public static RequestMatcher[] getPermittedMatchers() {
        return PERMITTED_MATCHERS;
    }
}
