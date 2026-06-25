package com.company.identitymanager.service;

import com.company.identitymanager.dto.response.UserProfileResponse;
import com.company.identitymanager.model.AppUser;
import com.company.identitymanager.model.Role;
import com.company.identitymanager.model.TenantMember;
import com.company.identitymanager.repository.AppUserRepository;
import com.company.identitymanager.repository.TenantMemberRepository;
import com.company.identitymanager.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private TenantMemberRepository memberRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void me_returnsProfileForExistingUser() {

        AppUser user = AppUser.builder()
                .id("user-1")
                .email("jane@example.com")
                .name("Jane Doe")
                .mfaConfigured(true)
                .build();

        TenantMember member = TenantMember.builder()
                .id("member-1")
                .userId("user-1")
                .role(Role.TENANT_ADMIN)
                .build();

        when(appUserRepository.findByEmail("jane@example.com"))
                .thenReturn(Optional.of(user));
        when(memberRepository.findByUserId("user-1"))
                .thenReturn(Optional.of(member));

        CurrentUser currentUser = CurrentUser.builder()
                .email("jane@example.com")
                .build();

        UserProfileResponse response = authService.me(currentUser);

        assertThat(response.getId()).isEqualTo("user-1");
        assertThat(response.getName()).isEqualTo("Jane Doe");
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getRole()).isEqualTo(Role.TENANT_ADMIN);
        assertThat(response.isMfaConfigured()).isTrue();
    }

    @Test
    void me_throwsWhenUserNotFound() {

        when(appUserRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        CurrentUser currentUser = CurrentUser.builder()
                .email("missing@example.com")
                .build();

        assertThatThrownBy(() -> authService.me(currentUser))
                .isInstanceOf(NoSuchElementException.class);
    }
}
