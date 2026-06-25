package com.company.identitymanager.email;

import com.company.identitymanager.model.EmailLog;
import com.company.identitymanager.repository.EmailLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SesEmailServiceTest {

    @Mock
    private SesClient sesClient;

    @Mock
    private EmailLogRepository emailLogRepository;

    @InjectMocks
    private SesEmailService sesEmailService;

    @Test
    void sendEmail_returnsSuccessAndLogsSent() {

        SendEmailResponse awsResponse = SendEmailResponse.builder()
                .messageId("msg-1")
                .build();

        when(sesClient.sendEmail(any(SendEmailRequest.class)))
                .thenReturn(awsResponse);

        EmailRequest request = EmailRequest.builder()
                .to("jane@example.com")
                .subject("Welcome")
                .body("<html>hi</html>")
                .build();

        EmailResponse response = sesEmailService.sendEmail(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessageId()).isEqualTo("msg-1");
        assertThat(response.getStatus()).isEqualTo("SENT");

        ArgumentCaptor<EmailLog> captor = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailLogRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("SENT");
        assertThat(captor.getValue().getRecipient()).isEqualTo("jane@example.com");
    }

    @Test
    void sendEmail_logsFailureAndThrowsOnAwsError() {

        when(sesClient.sendEmail(any(SendEmailRequest.class)))
                .thenThrow(new RuntimeException("SES unavailable"));

        EmailRequest request = EmailRequest.builder()
                .to("jane@example.com")
                .subject("Welcome")
                .body("<html>hi</html>")
                .build();

        assertThatThrownBy(() -> sesEmailService.sendEmail(request))
                .isInstanceOf(EmailException.class);

        ArgumentCaptor<EmailLog> captor = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailLogRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("FAILED");
    }
}
