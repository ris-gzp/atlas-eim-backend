package com.company.identitymanager.audit;

public final class AuditConstants {

    private AuditConstants() {
    }

    public static final String TENANT_CREATED =
            "TENANT_CREATED";

    public static final String USER_INVITED =
            "USER_INVITED";

    public static final String USER_PASSWORD_SET =
            "USER_PASSWORD_SET";

    public static final String MFA_CONFIGURED =
            "MFA_CONFIGURED";

    public static final String LOGIN_SUCCESS =
            "LOGIN_SUCCESS";

    public static final String LOGIN_FAILURE =
            "LOGIN_FAILURE";

    public static final String USER_REVOKED =
            "USER_REVOKED";

    public static final String LOGOUT =
            "LOGOUT";
}