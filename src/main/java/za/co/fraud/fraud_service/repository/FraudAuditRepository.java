package za.co.fraud.fraud_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.fraud.fraud_service.constant.FraudDecisionStatus;
import za.co.fraud.fraud_service.entity.FraudAuditDocument;

import java.util.List;
import java.util.Optional;

public interface FraudAuditRepository extends MongoRepository<FraudAuditDocument, String> {
    Page<FraudAuditDocument> findByUserId(Pageable pageable, String userId);
    Page<FraudAuditDocument> findByStatus(Pageable pageable,FraudDecisionStatus status);
    Optional<FraudAuditDocument> findByTransactionId(String transactionId);
    boolean existsByTransactionId(String transactionId);
}
