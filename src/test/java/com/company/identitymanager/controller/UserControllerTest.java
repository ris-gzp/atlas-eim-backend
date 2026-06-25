package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.InviteUserRequest;
import com.company.identitymanager.dto.response.UserResponse;
import com.company.identitymanager.model.Role;
import com.company.identitymanager.security.CurrentUser;
import com.company.identitymanager.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UsernamePasswordAuthenticationToken authFor(String role) {

        CurrentUser currentUser = CurrentUser.builder()
                .sub("sub-1")
                .email("admin@example.com")
                .tenantId("tenant-1")
                .role(role)
                .build();

        return new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Test
    void inviteUser_succeedsForTenantAdmin() throws Exception {

        InviteUserRequest request = new InviteUserRequest();
        request.setName("New User");
        request.setEmail("new@example.com");
        request.setRole(Role.SENIOR_MANAGER);

        when(userService.inviteUser(any(), eq("tenant-1"), eq("admin@example.com")))
                .thenReturn(
                        UserResponse.builder()
                                .id("user-1")
                                .name("New User")
                                .email("new@example.com")
                                .role(Role.SENIOR_MANAGER)
                                .mfaConfigured(false)
                                .build()
                );

        mockMvc.perform(
                post("/api/users")
                        .with(authentication(authFor("TENANT_ADMIN")))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void inviteUser_forbiddenForNonAdminRole() throws Exception {

        InviteUserRequest request = new InviteUserRequest();
        request.setName("New User");
        request.setEmail("new@example.com");
        request.setRole(Role.SENIOR_MANAGER);

        mockMvc.perform(
                post("/api/users")
                        .with(authentication(authFor("CUSTOMER_FACING")))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isForbidden());
    }

    @Test
    void inviteUser_rejectsInvalidEmail() throws Exception {

        InviteUserRequest request = new InviteUserRequest();
        request.setName("New User");
        request.setEmail("not-an-email");
        request.setRole(Role.SENIOR_MANAGER);

        mockMvc.perform(
                post("/api/users")
                        .with(authentication(authFor("TENANT_ADMIN")))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsers_returnsListForTenantAdmin() throws Exception {

        when(userService.getUsers()).thenReturn(
                List.of(
                        UserResponse.builder()
                                .id("user-1")
                                .name("Jane Doe")
                                .email("jane@example.com")
                                .role(Role.TENANT_ADMIN)
                                .mfaConfigured(true)
                                .build()
                )
        );

        mockMvc.perform(
                get("/api/users")
                        .with(authentication(authFor("TENANT_ADMIN")))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("jane@example.com"));
    }
}
