package com.company.identitymanager.security;

import com.company.identitymanager.cognito.CognitoConfig;
import com.company.identitymanager.exception.UnauthorizedException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtValidatorTest {

    private static final String ISSUER = "https://cognito-idp.ap-south-1.amazonaws.com/test-pool";

    private JwtValidator jwtValidator;
    private RSAPrivateKey signingKey;
    private RSAPrivateKey otherKey;

    @BeforeEach
    void setUp() throws Exception {

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        signingKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(signingKey)
                .keyID("test-key")
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource)
        );
        processor.setJWTClaimsSetVerifier(
                new DefaultJWTClaimsVerifier<>(
                        new JWTClaimsSet.Builder().issuer(ISSUER).build(),
                        Set.of("sub", "exp")
                )
        );

        jwtValidator = new JwtValidator(new CognitoConfig());
        jwtValidator.jwtProcessor = processor;
        jwtValidator.issuer = ISSUER;

        otherKey = (RSAPrivateKey) KeyPairGenerator.getInstance("RSA")
                .generateKeyPair().getPrivate();
    }

    private String signToken(RSAPrivateKey key, JWTClaimsSet claims) throws JOSEException {

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("test-key").build(),
                claims
        );
        signedJWT.sign(new RSASSASigner(key));
        return signedJWT.serialize();
    }

    private JWTClaimsSet validClaims() {

        return new JWTClaimsSet.Builder()
                .subject(UUID.randomUUID().toString())
                .issuer(ISSUER)
                .claim("email", "jane@example.com")
                .claim("custom:tenant_id", "tenant-1")
                .claim("custom:role", "TENANT_ADMIN")
                .expirationTime(new Date(System.currentTimeMillis() + 60_000))
                .build();
    }

    @Test
    void validate_acceptsProperlySignedToken() throws Exception {

        String token = signToken(signingKey, validClaims());

        JwtClaims claims = jwtValidator.validate(token);

        assertThat(claims.getEmail()).isEqualTo("jane@example.com");
        assertThat(claims.getTenantId()).isEqualTo("tenant-1");
        assertThat(claims.getRole()).isEqualTo("TENANT_ADMIN");
    }

    @Test
    void validate_rejectsTokenSignedWithWrongKey() throws Exception {

        String token = signToken(otherKey, validClaims());

        assertThatThrownBy(() -> jwtValidator.validate(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void validate_rejectsExpiredToken() throws Exception {

        JWTClaimsSet expired = new JWTClaimsSet.Builder()
                .subject(UUID.randomUUID().toString())
                .issuer(ISSUER)
                .expirationTime(new Date(System.currentTimeMillis() - 60_000))
                .build();

        String token = signToken(signingKey, expired);

        assertThatThrownBy(() -> jwtValidator.validate(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void validate_rejectsWrongIssuer() throws Exception {

        JWTClaimsSet wrongIssuer = new JWTClaimsSet.Builder()
                .subject(UUID.randomUUID().toString())
                .issuer("https://evil.example.com")
                .expirationTime(new Date(System.currentTimeMillis() + 60_000))
                .build();

        String token = signToken(signingKey, wrongIssuer);

        assertThatThrownBy(() -> jwtValidator.validate(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void validate_rejectsGarbageToken() {

        assertThatThrownBy(() -> jwtValidator.validate("not-a-jwt"))
                .isInstanceOf(UnauthorizedException.class);
    }
}
