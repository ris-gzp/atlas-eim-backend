package com.company.identitymanager.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "app_users")
public class AppUser {

    @Id
    private String id;
    @Indexed(unique = true)
    private String cognitoSub;
    @Indexed(unique = true)
    private String email;

    private String name;

    private boolean mfaConfigured;

    private Instant createdAt;

    private Instant updatedAt;
}