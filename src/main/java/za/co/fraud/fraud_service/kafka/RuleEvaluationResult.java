package za.co.fraud.fraud_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import za.co.fraud.fraud_service.constant.RuleType;

@AllArgsConstructor
@Getter
public class RuleEvaluationResult {
    RuleType ruleId;
    boolean triggered;
    String description;
    int riskScoreContribution;
}
