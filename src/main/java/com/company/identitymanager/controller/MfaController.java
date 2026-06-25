package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.VerifyMfaRequest;
import com.company.identitymanager.dto.response.ApiResponse;
import com.company.identitymanager.security.CurrentUser;
import com.company.identitymanager.service.MfaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;
    
    @Operation(summary = "Verify MFA setup")
    @PostMapping("/verify")
    public ApiResponse verify(
            @Valid @RequestBody
            VerifyMfaRequest request,
            Authentication authentication) {

        CurrentUser currentUser =
                (CurrentUser)
                        authentication.getPrincipal();

        mfaService.verifyMfa(
                currentUser.getEmail()
        );

        return ApiResponse.builder()
                .success(true)
                .message(
                        "MFA configured successfully"
                )
                .build();
    }
}