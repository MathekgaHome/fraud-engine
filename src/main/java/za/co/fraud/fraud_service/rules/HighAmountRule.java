package za.co.fraud.fraud_service.rules;

import org.springframework.stereotype.Component;
import za.co.fraud.fraud_service.constant.ErrorMessages;
import za.co.fraud.fraud_service.constant.RuleType;
import za.co.fraud.fraud_service.kafka.RuleEvaluationResult;
import za.co.fraud.fraud_service.kafka.TransactionEvent;

import java.math.BigDecimal;

@Component
public class HighAmountRule implements FraudRule {

    // TODO this should be stored in the database. auth jack
    private static final BigDecimal THRESHOLD = new BigDecimal("10000.00");

    public RuleEvaluationResult evaluate(TransactionEvent event) {
        boolean triggered = event.amount().compareTo(THRESHOLD) > 0;

        return new RuleEvaluationResult(
                RuleType.RULE_HIGH_AMOUNT,
                triggered,
                triggered ? String.format(ErrorMessages.EXCEED_AMOUNT,THRESHOLD) : "Normal amount",
                triggered ? 50 : 0
        );
    }
}
