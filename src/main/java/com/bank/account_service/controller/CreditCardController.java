package com.bank.account_service.controller;

import com.bank.account_service.dto.card.CreditCardApplyRequest;
import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardResponse;
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



    @PostMapping("/account/credit-cards/apply")
    public ResponseEntity<Map<String, Object>> apply(
            @RequestBody CreditCardApplyRequest request
    ) {
        AuthUser user = getUser();

        UUID requestId = service.applyCreditCard(
                user.getCustomerId(),
                request.getCardHolderName()
        );

        // 🟢 AUTO APPROVED (no request created)
        if (requestId == null) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "APPROVED",
                            "message", "Credit card issued automatically based on transaction history"
                    )
            );
        }

        // 🔴 MANUAL APPROVAL REQUIRED
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "PENDING",
                        "requestId", requestId,
                        "message", "Credit card request submitted for approval"
                ));
    }

    @GetMapping("/account/credit-cards")
    public List<CreditCardResponse> myCards() {
        return service.getCards(getUser().getCustomerId());
    }

    // ---------------- ADMIN ----------------

    @PostMapping("/admin/credit-cards/approve/{requestId}")
    public CreditCardIssueResponse approve(@PathVariable UUID requestId) {
        return service.approveRequest(requestId);
    }

    @PostMapping("/admin/credit-cards/reject/{requestId}")
    public ResponseEntity<Map<String, String>> reject(
            @PathVariable UUID requestId,
            @RequestParam String reason
    ) {
        service.rejectRequest(requestId, reason);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of(
                        "status", "REJECTED",
                        "reason", reason
                ));
    }

    private AuthUser getUser() {
        return (AuthUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
