package com.company.identitymanager.repository;

import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository
        extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByAction(AuditAction action);

    List<AuditLog> findByActor(String actor);

    List<AuditLog> findByTimestampBetween(
            Instant start,
            Instant end
    );
}