package com.neobank.neobank_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.neobank_backend.common.api.ErrorResponse;
import com.neobank.neobank_backend.common.constants.ErrorCodes;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.time.Instant;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            String correlationId = MDC.get("correlationId");
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = request.getHeader("X-Correlation-Id");
            }

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .errorCode(ErrorCodes.UNAUTHORIZED)
                    .message("Authentication required")
                    .path(request.getRequestURI())
                    .correlationId(correlationId)
                    .build();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String correlationId = MDC.get("correlationId");
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = request.getHeader("X-Correlation-Id");
            }

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.FORBIDDEN.value())
                    .errorCode(ErrorCodes.FORBIDDEN)
                    .message("Access denied")
                    .path(request.getRequestURI())
                    .correlationId(correlationId)
                    .build();

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        org.springframework.security.core.userdetails.UserDetails admin = org.springframework.security.core.userdetails.User.withUsername("admin")
                .password("{noop}Admin@123")
                .roles("ADMIN", "OPERATIONS", "AUDITOR")
                .build();
        org.springframework.security.core.userdetails.UserDetails ops = org.springframework.security.core.userdetails.User.withUsername("ops")
                .password("{noop}Ops@123")
                .roles("OPERATIONS")
                .build();
        org.springframework.security.core.userdetails.UserDetails auditor = org.springframework.security.core.userdetails.User.withUsername("auditor")
                .password("{noop}Auditor@123")
                .roles("AUDITOR")
                .build();
        return new org.springframework.security.provisioning.InMemoryUserDetailsManager(admin, ops, auditor);
    }
}
