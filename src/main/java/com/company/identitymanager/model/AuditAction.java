package com.company.identitymanager.model;

public enum AuditAction {

    TENANT_CREATED,
    USER_INVITED,
    USER_PASSWORD_SET,
    MFA_CONFIGURED,
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    USER_REVOKED,
    LOGOUT
}