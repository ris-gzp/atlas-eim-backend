package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.cognito.*;
import com.company.identitymanager.dto.request.CreateTenantRequest;
import com.company.identitymanager.dto.response.TenantResponse;
import com.company.identitymanager.email.EmailRequest;
import com.company.identitymanager.email.EmailService;
import com.company.identitymanager.email.EmailTemplateService;
import com.company.identitymanager.exception.BadRequestException;
import com.company.identitymanager.model.*;
import com.company.identitymanager.repository.TenantConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantConfigRepository tenantConfigRepository;
    private final CognitoService cognitoService;
    private final EmailService emailService;
    private final EmailTemplateService templateService;
    private final AuditService auditService;

    public TenantResponse createTenant(
            CreateTenantRequest request) {

    	log.info("Creating tenant with slug: {}", request.getSlug());

    	if (tenantConfigRepository.existsBySlug(
    	        request.getSlug())) {

    	    log.warn("Tenant slug already exists: {}", request.getSlug());

    	    throw new BadRequestException(
    	            "Tenant slug already exists");
    	}
    	String databaseName =
    	        "tenant_" + request.getSlug();

    	TenantConfig tenant =
    	        TenantConfig.builder()
    	                .slug(request.getSlug())
    	                .displayName(request.getDisplayName())
    	                .dbName(databaseName)
    	                .status(TenantStatus.ACTIVE)
    	                .build();

    	tenant = tenantConfigRepository.save(tenant);
    	
    	cognitoService.createUser(

    	        CognitoUserRequest.builder()

    	                .email(request.getAdminEmail())

    	                .name(request.getAdminName())

    	                .tenantId(request.getSlug())

    	                .role(
    	                        Role.TENANT_ADMIN.name()
    	                )

    	                .temporaryPassword(
    	                        "Temp@123456"
    	                )

    	                .build()
    	);
    	
    	String html =
    	        templateService.buildWelcomeEmail(
    	                request.getAdminName(),
    	                "https://app.company.com/login"
    	        );

    	emailService.sendEmail(
    	        EmailRequest.builder()
    	                .to(request.getAdminEmail())
    	                .subject("Welcome")
    	                .body(html)
    	                .build()
    	);

    	auditService.audit(
    	        AuditAction.TENANT_CREATED,
    	        request.getAdminEmail(),
    	        tenant.getSlug(),
    	        Map.of("tenantId", tenant.getId())
    	);

    	log.info("Tenant created successfully with slug: {}", tenant.getSlug());

    	return TenantResponse.builder()
    	        .tenantId(tenant.getId())
    	        .displayName(tenant.getDisplayName())
    	        .slug(tenant.getSlug())
    	        .dbName(tenant.getDbName())
    	        .adminEmail(request.getAdminEmail())
    	        .status(tenant.getStatus().name())
    	        .build();
    }
}
