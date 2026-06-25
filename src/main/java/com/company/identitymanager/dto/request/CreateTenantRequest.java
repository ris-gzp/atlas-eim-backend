package com.company.identitymanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTenantRequest {

    @NotBlank(message = "Tenant name is required")
    private String displayName;

    @NotBlank(message = "Tenant slug is required")
    private String slug;

    @NotBlank(message = "Admin name is required")
    private String adminName;

    @Email
    @NotBlank(message = "Admin email is required")
    private String adminEmail;
}