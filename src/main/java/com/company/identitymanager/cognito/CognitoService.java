package com.company.identitymanager.cognito;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;

    private final CognitoConfig cognitoConfig;

    public CognitoUserResponse createUser(
            CognitoUserRequest request) {

        log.debug("Creating Cognito user with email: {}", request.getEmail());

        try {

            AdminCreateUserRequest cognitoRequest =
                    AdminCreateUserRequest.builder()

                    .userPoolId(
                            cognitoConfig.getUserPoolId()
                    )

                    .username(
                            request.getEmail()
                    )

                    .temporaryPassword(
                            request.getTemporaryPassword()
                    )

                    .userAttributes(

                            AttributeType.builder()
                                    .name("email")
                                    .value(request.getEmail())
                                    .build(),

                            AttributeType.builder()
                                    .name("name")
                                    .value(request.getName())
                                    .build(),

                            AttributeType.builder()
                                    .name("custom:tenant_id")
                                    .value(request.getTenantId())
                                    .build(),

                            AttributeType.builder()
                                    .name("custom:role")
                                    .value(request.getRole())
                                    .build()
                    )

                    .build();

            AdminCreateUserResponse response =
                    cognitoClient.adminCreateUser(
                            cognitoRequest
                    );

            log.info("Cognito user created with email: {}", request.getEmail());

            return CognitoUserResponse.builder()
                    .username(
                            response.user().username()
                    )
                    .email(
                            request.getEmail()
                    )
                    .status(
                            response.user()
                                    .userStatusAsString()
                    )
                    .build();

        } catch (Exception ex) {

            log.error(
                    "Failed to create Cognito user with email: {}",
                    request.getEmail(),
                    ex
            );

            throw new CognitoException(
                    "Failed to create user",
                    ex
            );
        }
    }
    
    public AdminGetUserResponse getUser(
            String email) {

        return cognitoClient.adminGetUser(

                AdminGetUserRequest.builder()

                        .userPoolId(
                                cognitoConfig.getUserPoolId()
                        )

                        .username(email)

                        .build()
        );
    }
    
    public void deleteUser(
            String email) {

        cognitoClient.adminDeleteUser(

                AdminDeleteUserRequest.builder()

                        .userPoolId(
                                cognitoConfig.getUserPoolId()
                        )

                        .username(email)

                        .build()
        );
    }
    
    public void globalSignOut(
            String email) {

        log.debug("Issuing global sign-out for email: {}", email);

        cognitoClient.adminUserGlobalSignOut(

                AdminUserGlobalSignOutRequest.builder()

                        .userPoolId(
                                cognitoConfig.getUserPoolId()
                        )

                        .username(email)

                        .build()
        );
    }
    
    public void disableUser(
            String email) {

        cognitoClient.adminDisableUser(

                AdminDisableUserRequest.builder()

                        .userPoolId(
                                cognitoConfig.getUserPoolId()
                        )

                        .username(email)

                        .build()
        );
    }
    
    
    public void enableUser(
            String email) {

        cognitoClient.adminEnableUser(

                AdminEnableUserRequest.builder()

                        .userPoolId(
                                cognitoConfig.getUserPoolId()
                        )

                        .username(email)

                        .build()
        );
    }
}