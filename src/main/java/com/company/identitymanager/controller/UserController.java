package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.InviteUserRequest;
import com.company.identitymanager.dto.response.UserResponse;
import com.company.identitymanager.security.CurrentUser;
import com.company.identitymanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @Operation(summary = "Invite user")
    @PostMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public UserResponse inviteUser(
            @Valid @RequestBody
            InviteUserRequest request,
            Authentication authentication) {

        CurrentUser currentUser =
                (CurrentUser)
                        authentication.getPrincipal();

        return userService.inviteUser(
                request,
                currentUser.getTenantId(),
                currentUser.getEmail()
        );
    }
    
    @Operation(summary = "List tenant users")
    @GetMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public List<UserResponse> getUsers() {

        return userService.getUsers();
    }
}