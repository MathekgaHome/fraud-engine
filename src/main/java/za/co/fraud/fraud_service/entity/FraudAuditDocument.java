package za.co.fraud.fraud_service.entity;

import lombok.AllArgsConstructor;
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

@Document(collection = "fraud_audits")
@Getter
@Setter
@AllArgsConstructor
public class FraudAuditDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionId;
    @Indexed
    private String userId;
    private int totalRiskScore;
    private FraudDecisionStatus status;
    private List<RuleEvaluationResult> violations;
    private LocalDateTime evaluatedAt;
    private String pdfStorageKey;
    @Field("created_date")
    @CreatedDate
    private LocalDateTime createdDate;
}
