package com.example.phase1_fams.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // or specify a more specific path
                .allowedOrigins("*") // Specify the allowed origins
                .allowedMethods("*") // Specify the allowed methods
                .allowedHeaders("*")
                .maxAge(3600); // Cache the CORS configuration for 1 hour
    }
}