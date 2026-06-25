package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.LogoutRequest;
import com.company.identitymanager.dto.response.UserProfileResponse;
import com.company.identitymanager.model.Role;
import com.company.identitymanager.security.CurrentUser;
import com.company.identitymanager.service.AuthService;
import com.company.identitymanager.service.LogoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private LogoutService logoutService;

    private UsernamePasswordAuthenticationToken authFor(CurrentUser currentUser) {

        return new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + currentUser.getRole()))
        );
    }

    @Test
    void me_returnsProfileForAuthenticatedUser() throws Exception {

        CurrentUser currentUser = CurrentUser.builder()
                .sub("sub-1")
                .email("jane@example.com")
                .tenantId("tenant-1")
                .role(Role.TENANT_ADMIN.name())
                .build();

        when(authService.me(org.mockito.ArgumentMatchers.any()))
                .thenReturn(
                        UserProfileResponse.builder()
                                .id("user-1")
                                .name("Jane Doe")
                                .email("jane@example.com")
                                .role(Role.TENANT_ADMIN)
                                .mfaConfigured(true)
                                .build()
                );

        mockMvc.perform(
                get("/api/auth/me")
                        .with(authentication(authFor(currentUser)))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.role").value("TENANT_ADMIN"));
    }

    @Test
    void me_returnsUnauthorizedWithoutAuthentication() throws Exception {

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_invokesLogoutServiceAndReturnsSuccess() throws Exception {

        CurrentUser currentUser = CurrentUser.builder()
                .sub("sub-1")
                .email("jane@example.com")
                .tenantId("tenant-1")
                .role(Role.TENANT_ADMIN.name())
                .build();

        LogoutRequest request = new LogoutRequest();
        request.setRefreshToken("refresh-token-value");

        mockMvc.perform(
                post("/api/auth/logout")
                        .with(authentication(authFor(currentUser)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(logoutService).logout("jane@example.com");
    }

    @Test
    void logout_rejectsMissingRefreshToken() throws Exception {

        CurrentUser currentUser = CurrentUser.builder()
                .sub("sub-1")
                .email("jane@example.com")
                .tenantId("tenant-1")
                .role(Role.TENANT_ADMIN.name())
                .build();

        LogoutRequest request = new LogoutRequest();

        mockMvc.perform(
                post("/api/auth/logout")
                        .with(authentication(authFor(currentUser)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }
}
