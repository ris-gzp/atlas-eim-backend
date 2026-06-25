package com.company.identitymanager.controller;

import com.company.identitymanager.dto.request.CreateTenantRequest;
import com.company.identitymanager.dto.response.TenantResponse;
import com.company.identitymanager.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sysadmin")
@RequiredArgsConstructor
public class SysAdminController {

    private final TenantService tenantService;

    @Operation(summary = "Create a new tenant")
    @PostMapping("/tenants")
    @PreAuthorize("hasRole('SYSADMIN')")
    public TenantResponse createTenant(
            @Valid @RequestBody
            CreateTenantRequest request) {

        return tenantService.createTenant(request);
    }
}