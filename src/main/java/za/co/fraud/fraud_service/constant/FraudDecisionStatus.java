package za.co.fraud.fraud_service.constant;

public enum FraudDecisionStatus {
    APPROVED("Approved"),
    REVIEW("Review"),
    REJECTED("Rejected");

    private final String fraudDecision;

    FraudDecisionStatus(final String fraudDecision) {
        this.fraudDecision = fraudDecision;
    }

    public String getFraudDecision() {
        return fraudDecision;
    }
}
