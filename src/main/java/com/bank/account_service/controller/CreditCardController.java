package com.bank.account_service.controller;

import com.bank.account_service.dto.card.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService service;

    // ================= CUSTOMER =================

    @PostMapping("/account/credit-cards/apply")
    public ResponseEntity<Map<String, Object>> apply(
            @RequestBody CreditCardApplyRequest request
    ) {
        AuthUser user = getUser();

        UUID requestId =
                service.applyCreditCard(user.getCustomerId(), request.getCardHolderName());

        if (requestId == null) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", "APPROVED",
                    "title", "Congratulations 🎉",
                    "message", "Your credit card has been approved instantly",
                    "description",
                    "Based on your transaction history, your credit card is approved automatically.",
                    "nextSteps",
                    "Check your dashboard to view card details and available limit"
            ));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "success", true,
                "status", "PENDING",
                "requestId", requestId.toString(),
                "title", "Application Submitted",
                "message", "Your credit card application is under review",
                "description",
                "Our team is reviewing your application. Approval usually takes 2–3 business days.",
                "nextSteps",
                "You will receive SMS and Email updates"
        ));
    }

    @GetMapping("/account/credit-cards/status")
    public ResponseEntity<CreditCardResponse> getStatus() {
        AuthUser user = getUser();
        return ResponseEntity.ok(
                service.getCardStatus(user.getCustomerId())
        );
    }

    @GetMapping("/admin/credit-cards/pending")
    public ResponseEntity<Map<String, Object>> getPendingRequests() {
        ensureAdmin();

        List<CreditCardRequest> pending = service.getPendingRequests();

        if (pending.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "No pending credit card applications",
                    "description", "All applications have been processed",
                    "count", 0,
                    "requests", pending
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message",
                pending.size() + " credit card application(s) pending approval",
                "description",
                "Review customer profile and credit history before approval",
                "count", pending.size(),
                "requests", pending
        ));
    }

    @PostMapping("/admin/credit-cards/approve/{requestId}")
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable UUID requestId
    ) {
        ensureAdmin();

        CreditCardIssueResponse response =
                service.approveRequest(requestId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "title", "Credit Card Approved",
                "message", "Credit card has been issued successfully",
                "description",
                "Customer can now view credit card details in dashboard",
                "data", response,
                "notification",
                "Customer notified via SMS and Email"
        ));
    }

    @PostMapping("/admin/credit-cards/reject/{requestId}")
    public ResponseEntity<Map<String, Object>> reject(
            @PathVariable UUID requestId,
            @RequestParam String reason
    ) {
        ensureAdmin();

        service.rejectRequest(requestId, reason);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "title", "Application Rejected",
                "message", "Credit card application rejected",
                "reason", reason,
                "description",
                "Customer has been notified about the rejection",
                "notification",
                "Customer notified via SMS and Email"
        ));
    }


    private AuthUser getUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof AuthUser authUser) {
            return authUser;
        }

        throw new RuntimeException("User not authenticated");
    }

    private void ensureAdmin() {
        AuthUser user = getUser();
        if (!user.isAdmin()) {
            throw new RuntimeException("Admin access required");
        }
    }
}
