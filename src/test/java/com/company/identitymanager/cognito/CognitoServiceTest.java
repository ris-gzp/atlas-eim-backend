package com.company.identitymanager.cognito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUserGlobalSignOutRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CognitoServiceTest {

    @Mock
    private CognitoIdentityProviderClient cognitoClient;

    @Mock
    private CognitoConfig cognitoConfig;

    @InjectMocks
    private CognitoService cognitoService;

    @Test
    void createUser_returnsMappedResponse() {

        when(cognitoConfig.getUserPoolId()).thenReturn("pool-1");

        UserType userType = UserType.builder()
                .username("jane@example.com")
                .userStatus("FORCE_CHANGE_PASSWORD")
                .build();

        AdminCreateUserResponse awsResponse = AdminCreateUserResponse.builder()
                .user(userType)
                .build();

        when(cognitoClient.adminCreateUser(any(AdminCreateUserRequest.class)))
                .thenReturn(awsResponse);

        CognitoUserRequest request = CognitoUserRequest.builder()
                .email("jane@example.com")
                .name("Jane Doe")
                .tenantId("tenant-1")
                .role("TENANT_ADMIN")
                .temporaryPassword("Temp@123456")
                .build();

        CognitoUserResponse response = cognitoService.createUser(request);

        assertThat(response.getUsername()).isEqualTo("jane@example.com");
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getStatus()).isEqualTo("FORCE_CHANGE_PASSWORD");
    }

    @Test
    void createUser_wrapsAwsExceptionsInCognitoException() {

        when(cognitoConfig.getUserPoolId()).thenReturn("pool-1");

        when(cognitoClient.adminCreateUser(any(AdminCreateUserRequest.class)))
                .thenThrow(new RuntimeException("AWS unavailable"));

        CognitoUserRequest request = CognitoUserRequest.builder()
                .email("jane@example.com")
                .name("Jane Doe")
                .tenantId("tenant-1")
                .role("TENANT_ADMIN")
                .temporaryPassword("Temp@123456")
                .build();

        assertThatThrownBy(() -> cognitoService.createUser(request))
                .isInstanceOf(CognitoException.class);
    }

    @Test
    void globalSignOut_invokesAwsClient() {

        when(cognitoConfig.getUserPoolId()).thenReturn("pool-1");

        cognitoService.globalSignOut("jane@example.com");

        verify(cognitoClient).adminUserGlobalSignOut(
                any(AdminUserGlobalSignOutRequest.class)
        );
    }
}
