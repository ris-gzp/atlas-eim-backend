package com.company.identitymanager.email;

public interface EmailService {

    EmailResponse sendEmail(
            EmailRequest request
    );
}