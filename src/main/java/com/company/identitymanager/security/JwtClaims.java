package com.company.identitymanager.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtClaims {

    private String sub;

    private String email;

    private String tenantId;

    private String role;
}