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
@Document(collection = "email_logs")
public class EmailLog {

    @Id
    private String id;
    @Indexed
    private String recipient;

    private String template;
    @Indexed
    private String status;

    private String providerMessageId;

    private String errorMessage;

    private Instant sentAt;
}