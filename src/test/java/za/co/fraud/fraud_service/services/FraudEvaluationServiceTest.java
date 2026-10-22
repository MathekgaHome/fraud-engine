package za.co.fraud.fraud_service.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.TemplateEngine;
import za.co.fraud.fraud_service.constant.FraudDecisionStatus;
import za.co.fraud.fraud_service.constant.RuleType;
import za.co.fraud.fraud_service.entity.FraudAuditDocument;
import za.co.fraud.fraud_service.exceptions.UserServiceException;
import za.co.fraud.fraud_service.kafka.RuleEvaluationResult;
import za.co.fraud.fraud_service.kafka.TransactionEvent;
import za.co.fraud.fraud_service.model.PageResourceResponse;
import za.co.fraud.fraud_service.repository.FraudAuditRepository;
import za.co.fraud.fraud_service.rules.FraudRule;
import za.co.fraud.fraud_service.services.storage.StorageFactory;
import za.co.fraud.fraud_service.services.storage.StorageProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudEvaluationServiceTest {

    @Mock
    private List<FraudRule> fraudRules;

    @Mock
    private FraudRule fraudRule;

    @Mock
    private FraudAuditRepository repository;

    @Mock
    private StorageFactory storageFactory;

    @Mock
    private StorageProvider storageProvider;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private FraudEvaluationService fraudEvaluationService;

    private TransactionEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new TransactionEvent(
                "tx-123",
                "user-456",
                new BigDecimal("500.00"),
                "ZA",
                "",
                LocalDateTime.now(),
                "12-90-32",
                "ZAR"
        );
    }


    @Test
    void processAndEvaluate_Success() {
        when(repository.existsByTransactionId("tx-123")).thenReturn(false);

        RuleEvaluationResult triggeredResult = new RuleEvaluationResult(RuleType.RULE_HIGH_AMOUNT, true, "High risk", 40);

        fraudRules = List.of(fraudRule);
        fraudEvaluationService = new FraudEvaluationService(fraudRules, repository, storageFactory, templateEngine);

        when(fraudRule.evaluate(sampleEvent)).thenReturn(triggeredResult);
//        when(repository.findByTransactionId("tx-123")).thenReturn(Optional.of(mock(FraudAuditDocument.class)));
//        when(templateEngine.process(eq("fraud-audit-report"), any(Context.class))).thenReturn("<html>Report</html>");
//        when(storageFactory.getActiveProvider()).thenReturn(storageProvider);
//        when(storageProvider.upload(any(), anyLong(), anyString(), anyString(), anyString())).thenReturn("audit-reports/fraud-audit-tx-123.pdf");

        fraudEvaluationService.processAndEvaluate(sampleEvent);

        ArgumentCaptor<FraudAuditDocument> documentCaptor = ArgumentCaptor.forClass(FraudAuditDocument.class);
        verify(repository, times(1)).save(documentCaptor.capture());

        FraudAuditDocument savedDoc = documentCaptor.getValue();
        assertThat(savedDoc.getTransactionId()).isEqualTo("tx-123");
        assertThat(savedDoc.getUserId()).isEqualTo("user-456");
        assertThat(savedDoc.getTotalRiskScore()).isEqualTo(40);
        assertThat(savedDoc.getStatus()).isEqualTo(FraudDecisionStatus.REVIEW);
        assertThat(savedDoc.getViolations()).hasSize(1);
    }

    @Test
    void processAndEvaluate_ThrowsException_WhenTransactionExists() {
        when(repository.existsByTransactionId("tx-123")).thenReturn(true);

        assertThatThrownBy(() -> fraudEvaluationService.processAndEvaluate(sampleEvent))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("TRANSACTION_DATA_EXISTS");

        verify(repository, never()).save(any());
    }

    @Test
    void getAuditsByUser_ReturnsPagedResponse() {
        FraudAuditDocument document = new FraudAuditDocument(
                "doc-id", "tx-123", "user-456", 0, FraudDecisionStatus.APPROVED,
                Collections.emptyList(), LocalDateTime.now(), "path", LocalDateTime.now()
        );
        Page<FraudAuditDocument> pageResult = new PageImpl<>(List.of(document));

        when(repository.findByUserId(any(Pageable.class), eq("user-456"))).thenReturn(pageResult);

        PageResourceResponse response = fraudEvaluationService.getAuditsByUser(0, 10, "user-456");

        assertThat(response).isNotNull();
        assertThat(response.getCount()).isEqualTo(1);
        assertThat(response.getData()).hasSize(1);
        verify(repository).findByUserId(any(Pageable.class), eq("user-456"));
    }
}
