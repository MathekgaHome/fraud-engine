package za.co.fraud.fraud_service.rest;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import za.co.fraud.fraud_service.constant.FraudDecisionStatus;
import za.co.fraud.fraud_service.kafka.TransactionEvent;
import za.co.fraud.fraud_service.model.PageResourceResponse;
import za.co.fraud.fraud_service.services.FraudEvaluationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/fraud-audits")
public class FraudQueryController {

    private final FraudEvaluationService evaluationService;

    @GetMapping
    public ResponseEntity<PageResourceResponse> getAllAudits(@RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "size", defaultValue = "12") int size) {

        return ResponseEntity.ok(evaluationService.getAll(page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResourceResponse> getAuditsByUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                @RequestParam(value = "size", defaultValue = "12") int size,
                                                                @PathVariable String userId) {
        return ResponseEntity.ok(evaluationService.getAuditsByUser(page, size, userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<PageResourceResponse> getAuditsByStatus(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                  @RequestParam(value = "size", defaultValue = "12") int size,
                                                                  @PathVariable FraudDecisionStatus status) {
        return ResponseEntity.ok(evaluationService.getAuditsByStatus(page, size, status));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> processAndEvaluate(@RequestBody TransactionEvent event) {
        evaluationService.processAndEvaluate(event);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }
}
