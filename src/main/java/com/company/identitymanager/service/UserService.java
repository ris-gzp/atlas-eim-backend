package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.cognito.*;
import com.company.identitymanager.dto.request.InviteUserRequest;
import com.company.identitymanager.dto.response.UserResponse;
import com.company.identitymanager.email.EmailRequest;
import com.company.identitymanager.email.EmailService;
import com.company.identitymanager.email.EmailTemplateService;
import com.company.identitymanager.exception.BadRequestException;
import com.company.identitymanager.model.*;
import com.company.identitymanager.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final TenantMemberRepository memberRepository;
    private final CognitoService cognitoService;
    private final EmailService emailService;
    private final EmailTemplateService templateService;
    private final AuditService auditService;

    public UserResponse inviteUser(
            InviteUserRequest request,
            String tenantId,
            String actorEmail) {

    	log.info(
    	        "Inviting user with email: {} to tenant: {}",
    	        request.getEmail(),
    	        tenantId
    	);

    	if (appUserRepository.existsByEmail(
    	        request.getEmail())) {

    	    log.warn("Duplicate email detected: {}", request.getEmail());

    	    throw new BadRequestException(
    	            "User already exists");
    	}
    	cognitoService.createUser(

    	        CognitoUserRequest.builder()

    	                .email(request.getEmail())

    	                .name(request.getName())

    	                .tenantId(tenantId)

    	                .role(
    	                        request.getRole().name()
    	                )

    	                .temporaryPassword(
    	                        "Temp@123456"
    	                )

    	                .build()
    	);
    	
    	AppUser user =
    	        AppUser.builder()
    	                .email(request.getEmail())
    	                .name(request.getName())
    	                .createdAt(Instant.now())
    	                .updatedAt(Instant.now())
    	                .build();

    	user = appUserRepository.save(user);
    	
    	memberRepository.save(

    	        TenantMember.builder()

    	                .userId(
    	                        user.getId()
    	                )

    	                .role(
    	                        request.getRole()
    	                )

    	                .build()
    	);
    	
    	String html =
    	        templateService.buildInviteEmail(
    	                request.getName(),
    	                request.getRole().name(),
    	                "https://app.company.com/login"
    	        );

    	emailService.sendEmail(
    	        EmailRequest.builder()
    	                .to(request.getEmail())
    	                .subject("Invitation")
    	                .body(html)
    	                .build()
    	);

    	auditService.audit(
    	        AuditAction.USER_INVITED,
    	        actorEmail,
    	        request.getEmail(),
    	        Map.of("tenantId", tenantId)
    	);

    	log.info("User invited successfully: {}", request.getEmail());

    	return UserResponse.builder()
    	        .id(user.getId())
    	        .name(user.getName())
    	        .email(user.getEmail())
    	        .role(request.getRole())
    	        .mfaConfigured(false)
    	        .build();
    	}
    
    
    public List<UserResponse> getUsers() {

        log.debug("Fetching all tenant users");

        return memberRepository.findAll()
                .stream()
                .map(member -> {

                    AppUser user =
                            appUserRepository
                                    .findById(
                                            member.getUserId()
                                    )
                                    .orElseThrow();

                    return UserResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .role(member.getRole())
                            .mfaConfigured(
                                    user.isMfaConfigured()
                            )
                            .build();
                })
                .toList();
    }
    }