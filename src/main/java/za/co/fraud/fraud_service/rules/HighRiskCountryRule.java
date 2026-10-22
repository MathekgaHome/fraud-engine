package za.co.fraud.fraud_service.rules;

import org.springframework.stereotype.Component;
import za.co.fraud.fraud_service.constant.ErrorMessages;
import za.co.fraud.fraud_service.constant.RuleType;
import za.co.fraud.fraud_service.kafka.RuleEvaluationResult;
import za.co.fraud.fraud_service.kafka.TransactionEvent;

import java.util.Set;

@Component
public class HighRiskCountryRule implements FraudRule {

    //TODO this should be stored in the database, coutries should be configurable. auth jack
    private static final Set<String> HIGH_RISK_COUNTRIES = Set.of("Iran", "Botswana", "Iraq");

    @Override
    public RuleEvaluationResult evaluate(TransactionEvent event) {
        boolean triggered = event.country() != null &&
                HIGH_RISK_COUNTRIES.stream().anyMatch(country -> country.equalsIgnoreCase(event.country()));

        return new RuleEvaluationResult(
                RuleType.RULE_HIGH_RISK_COUNTRY,
                triggered,
                triggered ? ErrorMessages.COUNTRY_NOT_SAFE : "Country safe",
                triggered ? 40 : 0
        );
    }
}
