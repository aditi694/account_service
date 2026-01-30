package com.bank.account_service.controller;

import com.bank.account_service.dto.card.*;
import com.bank.account_service.entity.CreditCardRequest;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CreditCardService;
import com.bank.account_service.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService service;

    @PostMapping("/account/credit-cards/apply")
    public ResponseEntity<Map<String, Object>> apply(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody CreditCardApplyRequest request
    ) {
        UUID requestId =
                service.applyCreditCard(user.getCustomerId(), request.getCardHolderName());

        if (requestId == null) {
            return ResponseEntity.ok(Map.of(
                    AppConstants.SUCCESS, true,
                    AppConstants.STATUS, AppConstants.STATUS_APPROVED,
                    AppConstants.TITLE, AppConstants.TITLE_APPROVED,
                    AppConstants.MESSAGE, AppConstants.MSG_APPROVED,
                    AppConstants.DESCRIPTION, AppConstants.DESC_APPROVED,
                    AppConstants.NEXT_STEPS, AppConstants.NEXT_APPROVED
            ));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                AppConstants.SUCCESS, true,
                AppConstants.STATUS, AppConstants.STATUS_PENDING,
                AppConstants.REQUEST_ID, requestId.toString(),
                AppConstants.TITLE, AppConstants.TITLE_PENDING,
                AppConstants.MESSAGE, AppConstants.MSG_PENDING,
                AppConstants.DESCRIPTION, AppConstants.DESC_PENDING,
                AppConstants.NEXT_STEPS, AppConstants.NEXT_PENDING
        ));
    }

    @GetMapping("/account/credit-cards/status")
    public CreditCardResponse getStatus(
            @AuthenticationPrincipal AuthUser user
    ) {
        return service.getCreditCardSummary(user.getCustomerId());
    }

    // -------- ADMIN --------

    @GetMapping("/admin/credit-cards/pending")
    public ResponseEntity<Map<String, Object>> pending(
            @AuthenticationPrincipal AuthUser user
    ) {
        ensureAdmin(user);

        List<CreditCardRequest> pending = service.getPendingRequests();

        return ResponseEntity.ok(Map.of(
                AppConstants.SUCCESS, true,
                AppConstants.COUNT, pending.size(),
                AppConstants.REQUESTS, pending
        ));
    }

    @PostMapping("/admin/credit-cards/approve/{requestId}")
    public Map<String, Object> approve(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID requestId
    ) {
        ensureAdmin(user);
        CreditCardIssueResponse response = service.approveRequest(requestId);

        return Map.of(
                AppConstants.SUCCESS, true,
                AppConstants.DATA, response
        );
    }

    @PostMapping("/admin/credit-cards/reject/{requestId}")
    public Map<String, Object> reject(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID requestId,
            @RequestParam String reason
    ) {
        ensureAdmin(user);
        service.rejectRequest(requestId, reason);

        return Map.of(AppConstants.SUCCESS, true);
    }

    private void ensureAdmin(AuthUser user) {
        if (!user.isAdmin()) {
            throw new RuntimeException("Admin access required");
        }
    }
}