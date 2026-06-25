package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.LogoutRequest;
import com.company.identitymanager.dto.response.ApiResponse;
import com.company.identitymanager.dto.response.UserProfileResponse;
import com.company.identitymanager.security.CurrentUser;
import com.company.identitymanager.service.AuthService;
import com.company.identitymanager.service.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final LogoutService logoutService;
    
    @Operation(summary = "Current logged in user")
    @GetMapping("/me")
    public UserProfileResponse me(
            Authentication authentication) {

        CurrentUser currentUser =
                (CurrentUser)
                        authentication.getPrincipal();

        return authService.me(currentUser);
    }
    
    @Operation(summary = "Logout current user")
    @PostMapping("/logout")
    public ApiResponse logout(
            Authentication authentication,
            @Valid @RequestBody
            LogoutRequest request) {

        CurrentUser currentUser =
                (CurrentUser)
                        authentication.getPrincipal();

        logoutService.logout(
                currentUser.getEmail()
        );

        return ApiResponse.builder()
                .success(true)
                .message("Logout successful")
                .build();
    }
}