package com.company.identitymanager.service;

import com.company.identitymanager.dto.response.UserProfileResponse;
import com.company.identitymanager.model.AppUser;
import com.company.identitymanager.model.TenantMember;
import com.company.identitymanager.repository.AppUserRepository;
import com.company.identitymanager.repository.TenantMemberRepository;
import com.company.identitymanager.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final TenantMemberRepository memberRepository;

    public UserProfileResponse me(
            CurrentUser currentUser) {

        log.debug("Fetching profile for email: {}", currentUser.getEmail());

        AppUser user =
                appUserRepository
                        .findByEmail(
                                currentUser.getEmail()
                        )
                        .orElseThrow();

        TenantMember member =
                memberRepository
                        .findByUserId(
                                user.getId()
                        )
                        .orElseThrow();

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(member.getRole())
                .mfaConfigured(
                        user.isMfaConfigured()
                )
                .build();
    }
    }