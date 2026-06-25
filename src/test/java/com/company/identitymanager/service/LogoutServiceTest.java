package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.cognito.CognitoService;
import com.company.identitymanager.model.AuditAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private CognitoService cognitoService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void logout_signsOutAndAudits() {

        logoutService.logout("jane@example.com");

        verify(cognitoService).globalSignOut("jane@example.com");

        verify(auditService).audit(
                AuditAction.LOGOUT,
                "jane@example.com",
                "jane@example.com",
                Map.of()
        );
    }
}
