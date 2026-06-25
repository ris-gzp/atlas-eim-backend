package com.company.identitymanager.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;
    @Indexed
    private AuditAction action;
    @Indexed
    private String actor;

    private String target;

    private Map<String, Object> metadata;
    @Indexed
    private Instant timestamp;
}