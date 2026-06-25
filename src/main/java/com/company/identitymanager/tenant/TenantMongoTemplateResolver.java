package com.company.identitymanager.tenant;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class TenantMongoTemplateResolver {

    private final MongoClient mongoClient;
    private final TenantRoutingService tenantRoutingService;

    private final ConcurrentMap<String, MongoTemplate> cache =
            new ConcurrentHashMap<>();

    public MongoTemplate getMongoTemplate() {

        String tenantSlug =
                TenantContext.getTenant();

        String databaseName =
                tenantRoutingService
                        .getDatabaseName(tenantSlug);

        return cache.computeIfAbsent(
                databaseName,
                db -> new MongoTemplate(
                        mongoClient,
                        db
                )
        );
    }
}