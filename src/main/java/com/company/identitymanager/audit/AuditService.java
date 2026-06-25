package com.company.identitymanager.audit;

import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.model.AuditLog;
import com.company.identitymanager.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    private final AuditMapper auditMapper;

    public void audit(
            AuditAction action,
            String actor,
            String target,
            Map<String, Object> metadata) {

        log.debug(
                "Recording audit event: action={} actor={} target={}",
                action,
                actor,
                target
        );

        AuditLog auditLog =
                auditMapper.toAuditLog(
                        action,
                        actor,
                        target,
                        metadata
                );

        auditLogRepository.save(auditLog);

        log.info(
                "Audit event saved: action={} actor={} target={}",
                action,
                actor,
                target
        );
    }
}