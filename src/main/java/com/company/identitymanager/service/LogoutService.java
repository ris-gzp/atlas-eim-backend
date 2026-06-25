package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.cognito.CognitoService;
import com.company.identitymanager.model.AuditAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final CognitoService cognitoService;
    private final AuditService auditService;

    public void logout(
            String email) {

        log.info("Logging out user with email: {}", email);

        cognitoService.globalSignOut(
                email
        );

        auditService.audit(
                AuditAction.LOGOUT,
                email,
                email,
                Map.of()
        );

        log.info("Logout completed for email: {}", email);
    }
    }