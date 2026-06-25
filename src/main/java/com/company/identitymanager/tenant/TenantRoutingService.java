package com.company.identitymanager.tenant;

import com.company.identitymanager.exception.TenantNotFoundException;
import com.company.identitymanager.model.TenantConfig;
import com.company.identitymanager.repository.TenantConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantRoutingService {

    private final TenantConfigRepository tenantConfigRepository;

    public TenantConfig getTenantConfig(String slug) {

        return tenantConfigRepository
                .findBySlug(slug)
                .orElseThrow(
                        () -> new TenantNotFoundException(slug)
                );
    }

    public String getDatabaseName(String slug) {

        return getTenantConfig(slug)
                .getDbName();
    }
}