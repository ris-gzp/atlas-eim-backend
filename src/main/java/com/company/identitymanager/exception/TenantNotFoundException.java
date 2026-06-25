package com.company.identitymanager.exception;

public class TenantNotFoundException extends ResourceNotFoundException {

    public TenantNotFoundException(String tenantSlug) {
        super("Tenant not found: " + tenantSlug);
    }
}