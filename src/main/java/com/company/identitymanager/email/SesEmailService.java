package com.company.identitymanager.email;

import com.company.identitymanager.model.EmailLog;
import com.company.identitymanager.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.Instant;


@Slf4j
@Service
@RequiredArgsConstructor
public class SesEmailService
        implements EmailService {

    private final SesClient sesClient;

    private final EmailLogRepository emailLogRepository;

    @Override
    public EmailResponse sendEmail(
            EmailRequest request) {

        log.debug("Sending email to: {} subject: {}", request.getTo(), request.getSubject());

        try {

            SendEmailRequest emailRequest =
                    SendEmailRequest.builder()

                            .destination(
                                    Destination.builder()
                                            .toAddresses(
                                                    request.getTo()
                                            )
                                            .build()
                            )

                            .message(
                                    Message.builder()

                                            .subject(
                                                    Content.builder()
                                                            .data(
                                                                    request.getSubject()
                                                            )
                                                            .build()
                                            )

                                            .body(
                                                    Body.builder()

                                                            .html(
                                                                    Content.builder()
                                                                            .data(
                                                                                    request.getBody()
                                                                            )
                                                                            .build()
                                                            )

                                                            .build()
                                            )

                                            .build()
                            )

                            .source(
                                    "noreply@yourdomain.com"
                            )

                            .build();

            SendEmailResponse response =
                    sesClient.sendEmail(
                            emailRequest
                    );
            
            emailLogRepository.save(

                    EmailLog.builder()
                            .recipient(
                                    request.getTo()
                            )
                            .template(
                                    request.getSubject()
                            )
                            .status("SENT")
                            .providerMessageId(
                                    response.messageId()
                            )
                            .sentAt(
                                    Instant.now()
                            )
                            .build()
            );

            log.info("Email sent to: {} messageId: {}", request.getTo(), response.messageId());

            return EmailResponse.builder()
                    .success(true)
                    .messageId(
                            response.messageId()
                    )
                    .status("SENT")
                    .build();
            
            
        } catch (Exception ex) {

            log.error("Failed to send email to: {}", request.getTo(), ex);

            emailLogRepository.save(

                    EmailLog.builder()
                            .recipient(
                                    request.getTo()
                            )
                            .template(
                                    request.getSubject()
                            )
                            .status("FAILED")
                            .errorMessage(
                                    ex.getMessage()
                            )
                            .sentAt(
                                    Instant.now()
                            )
                            .build()
            );

            throw new EmailException(
                    "Failed to send email",
                    ex
            );
        }
    }
}