package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.model.AppUser;
import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MfaServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MfaService mfaService;

    @Test
    void verifyMfa_marksUserConfiguredAndAudits() {

        AppUser user = AppUser.builder()
                .id("user-1")
                .email("jane@example.com")
                .mfaConfigured(false)
                .build();

        when(appUserRepository.findByEmail("jane@example.com"))
                .thenReturn(Optional.of(user));

        mfaService.verifyMfa("jane@example.com");

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(captor.capture());
        assertThat(captor.getValue().isMfaConfigured()).isTrue();

        verify(auditService).audit(
                AuditAction.MFA_CONFIGURED,
                "jane@example.com",
                "jane@example.com",
                Map.of()
        );
    }

    @Test
    void verifyMfa_throwsWhenUserNotFound() {

        when(appUserRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> mfaService.verifyMfa("missing@example.com"))
                .isInstanceOf(NoSuchElementException.class);
    }
}
