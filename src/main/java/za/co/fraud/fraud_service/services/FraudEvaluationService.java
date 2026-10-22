package za.co.fraud.fraud_service.services;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import za.co.fraud.fraud_service.constant.ErrorMessagesLocale;
import za.co.fraud.fraud_service.constant.FraudDecisionStatus;
import za.co.fraud.fraud_service.entity.FraudAuditDocument;
import za.co.fraud.fraud_service.exceptions.UserServiceException;
import za.co.fraud.fraud_service.kafka.RuleEvaluationResult;
import za.co.fraud.fraud_service.kafka.TransactionEvent;
import za.co.fraud.fraud_service.model.FraudAuditDocumentDto;
import za.co.fraud.fraud_service.model.PageResourceResponse;
import za.co.fraud.fraud_service.repository.FraudAuditRepository;
import za.co.fraud.fraud_service.rules.FraudRule;
import za.co.fraud.fraud_service.services.storage.StorageFactory;
import za.co.fraud.fraud_service.services.storage.StorageProvider;
import za.co.fraud.fraud_service.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
@Slf4j
public class FraudEvaluationService {

    private final List<FraudRule> fraudRules;
    private final FraudAuditRepository repository;
    private final StorageFactory storageFactory;
    private final TemplateEngine templateEngine;

    @Cacheable(value = "auditsByUser", key = "#userId + '-' + #page + '-' + #size")
    public PageResourceResponse getAuditsByUser(final int page, final int size,final String userId) {
        return convertResources(repository.findByUserId(Util.getPageable(page, size, Sort.Direction.DESC, "created_date"),userId));
    }

    @Cacheable(value = "auditsByStatus", key = "#status + '-' + #page + '-' + #size")
    public PageResourceResponse getAuditsByStatus(final int page, final int size, final FraudDecisionStatus status) {

        return convertResources(repository.findByStatus(Util.getPageable(page, size, Sort.Direction.DESC, "created_date"), status));
    }

    @Cacheable(value = "auditsAll", key = "#page + '-' + #size")
    public PageResourceResponse getAll(final int page, final int size) {

        return convertResources(repository.findAll(Util.getPageable(page, size, Sort.Direction.DESC, "created_date")));
    }

    @CacheEvict(value = {"auditsByUser", "auditsByStatus", "auditsAll"}, allEntries = true)
    public void processAndEvaluate(final TransactionEvent event) {

        if(repository.existsByTransactionId(event.transactionId())) {
            throw  new UserServiceException(ErrorMessagesLocale.TRANSACTION_DATA_EXISTS, "Transaction Id already exists on the database");
        }

        List<RuleEvaluationResult> violations = fraudRules.stream()
                .map(rule -> rule.evaluate(event))
                .filter(RuleEvaluationResult::isTriggered)
                .toList();

        int totalRiskScore = violations.stream()
                .mapToInt(result -> result.getRiskScoreContribution())
                .sum();

        FraudDecisionStatus status = totalRiskScore >= 70 ? FraudDecisionStatus.REJECTED
                : totalRiskScore > 0 ? FraudDecisionStatus.REVIEW
                : FraudDecisionStatus.APPROVED;

        repository.save(new FraudAuditDocument(null,
                event.transactionId(),
                event.userId(),
                totalRiskScore,
                status,
                violations,
                LocalDateTime.now(),
                null,
                null
        ));

  //      generatePDF(event.transactionId());

    }

    public void generatePDF(final String transactionId) {
        FraudAuditDocument auditDocument = repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Audit document not found for transaction: " + transactionId));

        Context context = new Context();
        context.setVariable("audit", auditDocument);

        String processedHtml = templateEngine.process("fraud-audit-report", context);
        byte[] pdfBytes = convertHtmlToPdf(processedHtml);

        StorageProvider provider = storageFactory.getActiveProvider();
        String fileName = "fraud-audit-" + transactionId + ".pdf";

        try (InputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
            String storedPath = provider.upload(inputStream, pdfBytes.length, "audit-reports", fileName, "application/pdf");

            auditDocument.setPdfStorageKey(storedPath);
            repository.save(auditDocument);

            log.info("Fraud audit PDF successfully uploaded to MinIO at path: {}", storedPath);
        } catch (Exception e) {
            log.error("Failed to upload audit PDF to MinIO for transaction {}: {}", transactionId, e.getMessage());
            throw new RuntimeException("PDF upload failed", e);
        }
    }

    private byte[] convertHtmlToPdf(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("HTML to PDF conversion failed: {}", e.getMessage());
            throw new RuntimeException("PDF generation error", e);
        }
    }

    public InputStream downloadAuditReport(final String transactionId) {
        FraudAuditDocument auditDocument = repository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Audit document not found for transaction: " + transactionId));

        String fileName = "fraud-audit-" + transactionId + ".pdf";

        StorageProvider provider = storageFactory.getActiveProvider();

        String providerKey = "audit-reports/" + fileName;

        return provider.download(providerKey);
    }

    private PageResourceResponse convertResources(final Page<FraudAuditDocument> results) {
        return PageResourceResponse.builder()
                .count(results.getTotalPages())
                .data(results.getContent().stream().map(this::convertToDto).collect(Collectors.toList()))
                .totalPages((int) results.getTotalElements()).build();
    }

    private FraudAuditDocumentDto convertToDto(final FraudAuditDocument document) {
        return FraudAuditDocumentDto.builder()
                .userId(document.getUserId())
                .status(document.getStatus())
                .pdfStorageKey(document.getPdfStorageKey())
                .evaluatedAt(document.getEvaluatedAt())
                .createdDate(document.getCreatedDate())
                .transactionId(document.getTransactionId())
                .violations(document.getViolations()).build();
    }
}
