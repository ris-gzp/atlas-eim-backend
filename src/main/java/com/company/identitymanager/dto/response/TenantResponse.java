package com.company.identitymanager.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TenantResponse {

    private String tenantId;

    private String displayName;

    private String slug;

    private String dbName;

    private String adminEmail;

    private String status;
}