package com.company.identitymanager.repository;

import com.company.identitymanager.model.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmailLogRepository
        extends MongoRepository<EmailLog, String> {

    List<EmailLog> findByRecipient(String recipient);

    List<EmailLog> findByStatus(String status);
}