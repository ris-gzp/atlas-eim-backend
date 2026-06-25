package com.company.identitymanager.security;

import com.company.identitymanager.logging.RequestLoggingFilter;
import com.company.identitymanager.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.
        BadCredentialsException;
import org.springframework.security.authentication.
        UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.
        SimpleGrantedAuthority;
import org.springframework.security.core.context.
        SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String header =
                    request.getHeader("Authorization");

            if (header != null &&
                    header.startsWith("Bearer ")) {

                String token =
                        header.substring(7);

                JwtClaims claims;

                try {
                    claims = jwtValidator.validate(token);
                } catch (Exception ex) {

                    log.warn(
                            "Rejected request with invalid JWT: endpoint={}",
                            request.getRequestURI()
                    );

                    throw new BadCredentialsException(
                            "Invalid JWT token",
                            ex
                    );
                }

                TenantContext.setTenant(
                        claims.getTenantId()
                );

                MDC.put(
                        RequestLoggingFilter.MDC_USER_ID,
                        claims.getSub()
                );

                log.debug(
                        "Authenticated request for user={} tenant={} role={}",
                        claims.getSub(),
                        claims.getTenantId(),
                        claims.getRole()
                );

                CurrentUser currentUser =
                        CurrentUser.builder()
                                .sub(claims.getSub())
                                .email(claims.getEmail())
                                .tenantId(claims.getTenantId())
                                .role(claims.getRole())
                                .build();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                currentUser,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" +
                                                claims.getRole()
                                        )
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);
            }

            filterChain.doFilter(
                    request,
                    response
            );

        } finally {

            TenantContext.clear();
        }
    }
}