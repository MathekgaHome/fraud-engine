package za.co.fraud.fraud_service.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        String transactionId,
        String userId,
        BigDecimal amount,
        String currency,
        String merchantCategory,
        LocalDateTime timestamp,
        String deviceId,
        String country
) {}
