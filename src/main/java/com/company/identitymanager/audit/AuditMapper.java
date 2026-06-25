package com.company.identitymanager.audit;

import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.model.AuditLog;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class AuditMapper {

    public AuditLog toAuditLog(
            AuditAction action,
            String actor,
            String target,
            Map<String, Object> metadata) {

        return AuditLog.builder()
                .action(action)
                .actor(actor)
                .target(target)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();
    }
}