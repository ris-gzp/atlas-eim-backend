package com.company.identitymanager.cognito;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CognitoUserRequest {

    private String email;

    private String name;

    private String temporaryPassword;

    private String role;

    private String tenantId;
}