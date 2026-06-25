package com.company.identitymanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.
        AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.
                    AuthenticationException ex)
            throws IOException {

        response.setStatus(401);
        response.setContentType(
                "application/json"
        );

        new ObjectMapper()
                .writeValue(
                        response.getOutputStream(),
                        Map.of(
                                "error",
                                "Unauthorized"
                        )
                );
    }
}