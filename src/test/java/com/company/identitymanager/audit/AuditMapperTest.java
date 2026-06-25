package com.company.identitymanager.audit;

import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.model.AuditLog;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuditMapperTest {

    private final AuditMapper auditMapper = new AuditMapper();

    @Test
    void toAuditLog_mapsAllFields() {

        AuditLog log = auditMapper.toAuditLog(
                AuditAction.USER_INVITED,
                "actor@example.com",
                "target@example.com",
                Map.of("tenantId", "tenant-1")
        );

        assertThat(log.getAction()).isEqualTo(AuditAction.USER_INVITED);
        assertThat(log.getActor()).isEqualTo("actor@example.com");
        assertThat(log.getTarget()).isEqualTo("target@example.com");
        assertThat(log.getMetadata()).containsEntry("tenantId", "tenant-1");
        assertThat(log.getTimestamp()).isNotNull();
    }
}
