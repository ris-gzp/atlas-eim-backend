package com.company.identitymanager.repository;

import com.company.identitymanager.model.TenantConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TenantConfigRepository
        extends MongoRepository<TenantConfig, String> {

    Optional<TenantConfig> findBySlug(String slug);

    boolean existsBySlug(String slug);
}