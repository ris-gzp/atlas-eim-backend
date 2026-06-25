package com.company.identitymanager.security;

import com.company.identitymanager.cognito.CognitoConfig;
import com.company.identitymanager.exception.UnauthorizedException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final CognitoConfig cognitoConfig;

    DefaultJWTProcessor<SecurityContext> jwtProcessor;

    String issuer;

    @PostConstruct
    void init() throws Exception {

        issuer = "https://cognito-idp." + cognitoConfig.getRegion()
                + ".amazonaws.com/" + cognitoConfig.getUserPoolId();

        URL jwksUrl = new URL(issuer + "/.well-known/jwks.json");

        JWKSource<SecurityContext> jwkSource =
                JWKSourceBuilder.create(jwksUrl).build();

        JWSVerificationKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(keySelector);
        processor.setJWTClaimsSetVerifier(
                new DefaultJWTClaimsVerifier<>(
                        new JWTClaimsSet.Builder().issuer(issuer).build(),
                        java.util.Set.of("sub", "exp")
                )
        );

        this.jwtProcessor = processor;
    }

    public JwtClaims validate(String token) {

        try {

            JWTClaimsSet claimsSet =
                    jwtProcessor.process(token, null);

            String sub = claimsSet.getSubject();
            String email = claimsSet.getStringClaim("email");
            String tenantId = claimsSet.getStringClaim("custom:tenant_id");
            String role = claimsSet.getStringClaim("custom:role");

            return JwtClaims.builder()
                    .sub(sub)
                    .email(email)
                    .tenantId(tenantId)
                    .role(role)
                    .build();

        } catch (Exception ex) {

            log.warn("JWT validation failed", ex);

            throw new UnauthorizedException("Invalid JWT token");
        }
    }
}
