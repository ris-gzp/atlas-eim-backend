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
@Document(collection = "tenant_members")
public class TenantMember {

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private Role role;
}           