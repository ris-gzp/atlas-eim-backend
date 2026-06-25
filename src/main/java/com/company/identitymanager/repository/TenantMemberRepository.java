package com.company.identitymanager.repository;

import com.company.identitymanager.model.Role;
import com.company.identitymanager.model.TenantMember;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TenantMemberRepository
        extends MongoRepository<TenantMember, String> {

    Optional<TenantMember> findByUserId(String userId);

    List<TenantMember> findByRole(Role role);

    boolean existsByUserId(String userId);
}