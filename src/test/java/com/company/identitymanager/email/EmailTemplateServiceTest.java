package com.company.identitymanager.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTemplateServiceTest {

    private final EmailTemplateService templateService = new EmailTemplateService();

    @Test
    void buildWelcomeEmail_includesNameAndLoginUrl() {

        String html = templateService.buildWelcomeEmail(
                "Jane Doe",
                "https://app.company.com/login"
        );

        assertThat(html)
                .contains("Jane Doe")
                .contains("https://app.company.com/login")
                .contains("Welcome to Enterprise Identity Manager");
    }

    @Test
    void buildInviteEmail_includesNameRoleAndLoginUrl() {

        String html = templateService.buildInviteEmail(
                "John Smith",
                "TENANT_ADMIN",
                "https://app.company.com/login"
        );

        assertThat(html)
                .contains("John Smith")
                .contains("TENANT_ADMIN")
                .contains("https://app.company.com/login");
    }
}
