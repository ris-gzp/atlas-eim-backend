package com.company.identitymanager.tenant;

import org.springframework.stereotype.Component;

@Component
public class TenantResolver {

    public String resolveTenant() {

        String tenant = TenantContext.getTenant();

        if (tenant == null || tenant.isBlank()) {
            throw new IllegalStateException(
                    "No tenant available in context"
            );
        }

        return tenant;
    }
}