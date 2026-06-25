package com.company.identitymanager.repository;

import com.company.identitymanager.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppUserRepository
        extends MongoRepository<AppUser, String> {

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByCognitoSub(String cognitoSub);

    boolean existsByEmail(String email);
}