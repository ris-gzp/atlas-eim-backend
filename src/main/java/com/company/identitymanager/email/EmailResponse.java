package com.company.identitymanager.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailResponse {

    private boolean success;

    private String messageId;

    private String status;
}