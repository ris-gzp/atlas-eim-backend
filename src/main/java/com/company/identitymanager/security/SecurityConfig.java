package com.company.identitymanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.
        annotation.method.configuration.
        EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.
        HttpSecurity;
import org.springframework.security.config.http.
        SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.
        UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter filter;

    private final CustomAuthenticationEntryPoint
            authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .exceptionHandling(exception ->
                    exception.authenticationEntryPoint(
                            authenticationEntryPoint
                    )
            )

            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/actuator/health"
                    )
                    .permitAll()

                    .requestMatchers(
                            "/api/sysadmin/**"
                    )
                    .hasRole("SYSADMIN")

                    .anyRequest()
                    .authenticated()
            )

            .addFilterBefore(
                    filter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}