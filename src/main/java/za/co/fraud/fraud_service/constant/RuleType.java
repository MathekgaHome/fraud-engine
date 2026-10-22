package za.co.fraud.fraud_service.constant;

public enum RuleType {

    RULE_HIGH_RISK_COUNTRY("High Risk Country"),
    RULE_HIGH_AMOUNT("High Amount");

    private final String ruleType;

    RuleType(final String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleType() {
        return ruleType;
    }
}
