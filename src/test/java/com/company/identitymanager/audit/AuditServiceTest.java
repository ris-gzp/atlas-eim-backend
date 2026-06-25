package com.company.identitymanager.audit;

import com.company.identitymanager.model.AuditAction;
import com.company.identitymanager.model.AuditLog;
import com.company.identitymanager.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditMapper auditMapper;

    @InjectMocks
    private AuditService auditService;

    @Test
    void audit_mapsAndSavesAuditLog() {

        AuditLog mappedLog = AuditLog.builder()
                .action(AuditAction.LOGIN_SUCCESS)
                .actor("actor@example.com")
                .target("actor@example.com")
                .metadata(Map.of())
                .build();

        org.mockito.Mockito.when(
                auditMapper.toAuditLog(
                        AuditAction.LOGIN_SUCCESS,
                        "actor@example.com",
                        "actor@example.com",
                        Map.of()
                )
        ).thenReturn(mappedLog);

        auditService.audit(
                AuditAction.LOGIN_SUCCESS,
                "actor@example.com",
                "actor@example.com",
                Map.of()
        );

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        assertThat(captor.getValue()).isEqualTo(mappedLog);
    }
}
