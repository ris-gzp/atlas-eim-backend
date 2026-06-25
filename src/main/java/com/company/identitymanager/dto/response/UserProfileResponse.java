package com.company.identitymanager.dto.response;

import com.company.identitymanager.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    private String id;

    private String name;

    private String email;

    private Role role;

    private boolean mfaConfigured;
}