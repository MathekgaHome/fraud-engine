package za.co.fraud.fraud_service.services.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import za.co.fraud.fraud_service.kafka.TransactionEvent;
import za.co.fraud.fraud_service.services.FraudEvaluationService;

@Component
@AllArgsConstructor
public class TransactionConsumer {

    private final FraudEvaluationService evaluationService;

    @KafkaListener(topics = "transactions-topic", groupId = "fraud-engine-group")
    public void consumeTransaction(TransactionEvent event) {
        evaluationService.processAndEvaluate(event);
    }

}
