package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.cognito.CognitoService;
import com.company.identitymanager.cognito.CognitoUserRequest;
import com.company.identitymanager.dto.request.CreateTenantRequest;
import com.company.identitymanager.dto.response.TenantResponse;
import com.company.identitymanager.email.EmailRequest;
import com.company.identitymanager.email.EmailService;
import com.company.identitymanager.email.EmailTemplateService;
import com.company.identitymanager.exception.BadRequestException;
import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.model.TenantConfig;
import com.company.identitymanager.model.TenantStatus;
import com.company.identitymanager.repository.TenantConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantConfigRepository tenantConfigRepository;

    @Mock
    private CognitoService cognitoService;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailTemplateService templateService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TenantService tenantService;

    @Test
    void createTenant_rejectsDuplicateSlug() {

        CreateTenantRequest request = new CreateTenantRequest();
        request.setSlug("acme");
        request.setDisplayName("Acme Corp");
        request.setAdminName("Admin");
        request.setAdminEmail("admin@acme.com");

        when(tenantConfigRepository.existsBySlug("acme")).thenReturn(true);

        assertThatThrownBy(() -> tenantService.createTenant(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Tenant slug already exists");

        verify(cognitoService, never()).createUser(any());
        verify(auditService, never()).audit(any(), any(), any(), any());
    }

    @Test
    void createTenant_createsTenantCognitoUserEmailAndAudit() {

        CreateTenantRequest request = new CreateTenantRequest();
        request.setSlug("acme");
        request.setDisplayName("Acme Corp");
        request.setAdminName("Admin User");
        request.setAdminEmail("admin@acme.com");

        when(tenantConfigRepository.existsBySlug("acme")).thenReturn(false);

        when(tenantConfigRepository.save(any(TenantConfig.class)))
                .thenAnswer(invocation -> {
                    TenantConfig config = invocation.getArgument(0);
                    config.setId("tenant-1");
                    return config;
                });

        when(templateService.buildWelcomeEmail("Admin User", "https://app.company.com/login"))
                .thenReturn("<html>welcome</html>");

        TenantResponse response = tenantService.createTenant(request);

        assertThat(response.getTenantId()).isEqualTo("tenant-1");
        assertThat(response.getSlug()).isEqualTo("acme");
        assertThat(response.getDbName()).isEqualTo("tenant_acme");
        assertThat(response.getStatus()).isEqualTo(TenantStatus.ACTIVE.name());

        ArgumentCaptor<CognitoUserRequest> cognitoCaptor =
                ArgumentCaptor.forClass(CognitoUserRequest.class);
        verify(cognitoService).createUser(cognitoCaptor.capture());
        assertThat(cognitoCaptor.getValue().getEmail()).isEqualTo("admin@acme.com");
        assertThat(cognitoCaptor.getValue().getTenantId()).isEqualTo("acme");

        ArgumentCaptor<EmailRequest> emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailService).sendEmail(emailCaptor.capture());
        assertThat(emailCaptor.getValue().getTo()).isEqualTo("admin@acme.com");

        verify(auditService).audit(
                eq(AuditAction.TENANT_CREATED),
                eq("admin@acme.com"),
                eq("acme"),
                any()
        );
    }
}
