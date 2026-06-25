package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.VerifyMfaRequest;
import com.company.identitymanager.model.Role;
import com.company.identitymanager.security.CurrentUser;
import com.company.identitymanager.service.MfaService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MfaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MfaService mfaService;

    private UsernamePasswordAuthenticationToken authFor() {

        CurrentUser currentUser = CurrentUser.builder()
                .sub("sub-1")
                .email("jane@example.com")
                .tenantId("tenant-1")
                .role(Role.TENANT_ADMIN.name())
                .build();

        return new UsernamePasswordAuthenticationToken(
                currentUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_TENANT_ADMIN"))
        );
    }

    @Test
    void verify_succeedsWithValidCode() throws Exception {

        VerifyMfaRequest request = new VerifyMfaRequest();
        request.setCode("123456");

        mockMvc.perform(
                post("/api/users/me/mfa/verify")
                        .with(authentication(authFor()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(mfaService).verifyMfa("jane@example.com");
    }

    @Test
    void verify_rejectsNonNumericCode() throws Exception {

        VerifyMfaRequest request = new VerifyMfaRequest();
        request.setCode("abcdef");

        mockMvc.perform(
                post("/api/users/me/mfa/verify")
                        .with(authentication(authFor()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest());
    }
}
