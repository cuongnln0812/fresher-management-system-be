package com.example.phase1_fams.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity

public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthFilter;

        private final AuthenticationProvider authenticationProvider;

        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(
                                                                "/api/v1/auth/authenticate",
                                                                "/v2/api-docs",
                                                                "/v3/api-docs",
                                                                "/v3/api-docs/**",
                                                                "/swagger-resources",
                                                                "/swagger-resources/**",
                                                                "/configuration/ui",
                                                                "/configuration/security",
                                                                "/swagger-ui.html",
                                                                "/webjars/**",
                                                                "/swagger-ui/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/auth/user-information",
                                                                "/api/v1/auth/change-password",
                                                        "/api/v1/auth//homepage-statistics",
                                                        "/api/v1/output-standard/**")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(this.jwtAuthenticationEntryPoint))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                // http.csrf(AbstractHttpConfigurer::disable);
                return http.build();
        }

}
