package za.co.fraud.fraud_service.rules;


import za.co.fraud.fraud_service.kafka.RuleEvaluationResult;
import za.co.fraud.fraud_service.kafka.TransactionEvent;

public interface FraudRule {
    RuleEvaluationResult evaluate(TransactionEvent event);
}
