package com.company.identitymanager.service;

import com.company.identitymanager.audit.AuditService;
import com.company.identitymanager.cognito.CognitoService;
import com.company.identitymanager.dto.request.InviteUserRequest;
import com.company.identitymanager.dto.response.UserResponse;
import com.company.identitymanager.email.EmailService;
import com.company.identitymanager.email.EmailTemplateService;
import com.company.identitymanager.exception.BadRequestException;
import com.company.identitymanager.model.AppUser;
import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.model.Role;
import com.company.identitymanager.model.TenantMember;
import com.company.identitymanager.repository.AppUserRepository;
import com.company.identitymanager.repository.TenantMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private TenantMemberRepository memberRepository;

    @Mock
    private CognitoService cognitoService;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailTemplateService templateService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    @Test
    void inviteUser_rejectsDuplicateEmail() {

        InviteUserRequest request = new InviteUserRequest();
        request.setEmail("dup@example.com");
        request.setName("Dup User");
        request.setRole(Role.SENIOR_MANAGER);

        when(appUserRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() ->
                userService.inviteUser(request, "tenant-1", "actor@example.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User already exists");

        verify(cognitoService, never()).createUser(any());
        verify(auditService, never()).audit(any(), any(), any(), any());
    }

    @Test
    void inviteUser_createsUserAndAudits() {

        InviteUserRequest request = new InviteUserRequest();
        request.setEmail("new@example.com");
        request.setName("New User");
        request.setRole(Role.SENIOR_MANAGER);

        when(appUserRepository.existsByEmail("new@example.com")).thenReturn(false);

        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> {
                    AppUser user = invocation.getArgument(0);
                    user.setId("user-1");
                    return user;
                });

        when(templateService.buildInviteEmail(
                "New User",
                Role.SENIOR_MANAGER.name(),
                "https://app.company.com/login"))
                .thenReturn("<html>invite</html>");

        UserResponse response = userService.inviteUser(
                request, "tenant-1", "actor@example.com");

        assertThat(response.getId()).isEqualTo("user-1");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getRole()).isEqualTo(Role.SENIOR_MANAGER);
        assertThat(response.isMfaConfigured()).isFalse();

        verify(memberRepository).save(any(TenantMember.class));

        verify(auditService).audit(
                eq(AuditAction.USER_INVITED),
                eq("actor@example.com"),
                eq("new@example.com"),
                any()
        );
    }

    @Test
    void getUsers_returnsMappedResponses() {

        TenantMember member = TenantMember.builder()
                .id("member-1")
                .userId("user-1")
                .role(Role.CUSTOMER_FACING)
                .build();

        AppUser user = AppUser.builder()
                .id("user-1")
                .email("jane@example.com")
                .name("Jane Doe")
                .mfaConfigured(true)
                .build();

        when(memberRepository.findAll()).thenReturn(List.of(member));
        when(appUserRepository.findById("user-1")).thenReturn(Optional.of(user));

        List<UserResponse> result = userService.getUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("jane@example.com");
        assertThat(result.get(0).getRole()).isEqualTo(Role.CUSTOMER_FACING);
        assertThat(result.get(0).isMfaConfigured()).isTrue();
    }
}
