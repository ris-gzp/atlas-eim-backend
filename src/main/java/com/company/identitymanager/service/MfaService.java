package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.model.AppUser;
import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

    private final AppUserRepository appUserRepository;
    private final AuditService auditService;

    public void verifyMfa(
            String email) {

        log.debug("Verifying MFA setup for email: {}", email);

        AppUser user =
                appUserRepository
                        .findByEmail(email)
                        .orElseThrow();

        user.setMfaConfigured(true);

        appUserRepository.save(user);

        auditService.audit(
                AuditAction.MFA_CONFIGURED,
                email,
                email,
                Map.of()
        );

        log.info("MFA configured for email: {}", email);
    }
    }