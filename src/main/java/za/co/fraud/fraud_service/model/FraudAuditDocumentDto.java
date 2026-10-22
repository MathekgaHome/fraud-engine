package za.co.fraud.fraud_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import za.co.fraud.fraud_service.constant.FraudDecisionStatus;
import za.co.fraud.fraud_service.kafka.RuleEvaluationResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FraudAuditDocumentDto {

    private String id;
    private String transactionId;
    private String userId;
    private int totalRiskScore;
    private FraudDecisionStatus status;
    private List<RuleEvaluationResult> violations;
    private LocalDateTime evaluatedAt;
    private String pdfStorageKey;
    private LocalDateTime createdDate;
}
