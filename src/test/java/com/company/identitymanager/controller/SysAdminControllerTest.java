package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.CreateTenantRequest;
import com.company.identitymanager.dto.response.TenantResponse;
import com.company.identitymanager.model.Role;
import com.company.identitymanager.security.CurrentUser;
import com.company.identitymanager.service.TenantService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SysAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TenantService tenantService;

    private UsernamePasswordAuthenticationToken authFor(String role) {

        CurrentUser currentUser = CurrentUser.builder()
                .sub("sub-1")
                .email("sysadmin@example.com")
                .role(role)
                .build();

        return new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Test
    void createTenant_succeedsForSysAdmin() throws Exception {

        CreateTenantRequest request = new CreateTenantRequest();
        request.setSlug("acme");
        request.setDisplayName("Acme Corp");
        request.setAdminName("Admin");
        request.setAdminEmail("admin@acme.com");

        when(tenantService.createTenant(any())).thenReturn(
                TenantResponse.builder()
                        .tenantId("tenant-1")
                        .slug("acme")
                        .displayName("Acme Corp")
                        .dbName("tenant_acme")
                        .adminEmail("admin@acme.com")
                        .status("ACTIVE")
                        .build()
        );

        mockMvc.perform(
                post("/api/sysadmin/tenants")
                        .with(authentication(authFor("SYSADMIN")))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("acme"));
    }

    @Test
    void createTenant_forbiddenForNonSysAdmin() throws Exception {

        CreateTenantRequest request = new CreateTenantRequest();
        request.setSlug("acme");
        request.setDisplayName("Acme Corp");
        request.setAdminName("Admin");
        request.setAdminEmail("admin@acme.com");

        mockMvc.perform(
                post("/api/sysadmin/tenants")
                        .with(authentication(authFor("TENANT_ADMIN")))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isForbidden());
    }

    @Test
    void createTenant_rejectsMissingSlug() throws Exception {

        CreateTenantRequest request = new CreateTenantRequest();
        request.setDisplayName("Acme Corp");
        request.setAdminName("Admin");
        request.setAdminEmail("admin@acme.com");

        mockMvc.perform(
                post("/api/sysadmin/tenants")
                        .with(authentication(authFor("SYSADMIN")))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }
}
