package com.api.videostreaming.configs;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Video-Streaming: BACKEND API'S", 
        version = "1.0", 
        description = "API Documentation for Video-Streaming."
    )
)
@SecurityScheme(
    name = "bearerAuth", 
    type = SecuritySchemeType.HTTP, 
    bearerFormat = "JWT", 
    scheme = "Bearer", 
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
    @Bean
	public GroupedOpenApi api() {
		return GroupedOpenApi.builder()
            .group("default")
            .packagesToScan("com.api.videostreaming.controllers")
            .build();
	}
}
