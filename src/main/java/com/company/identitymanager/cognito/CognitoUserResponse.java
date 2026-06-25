package com.company.identitymanager.cognito;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CognitoUserResponse {

    private String username;

    private String email;

    private String cognitoSub;

    private String status;
}