package com.company.identitymanager.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "tenant_configs")
public class TenantConfig {

    @Id
    @Indexed(unique = true)
    private String id;

    private String slug;

    private String displayName;

    private String dbName;

    private TenantStatus status;
}