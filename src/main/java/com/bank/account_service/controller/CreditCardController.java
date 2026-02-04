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

        Map<String, Object> response = new HashMap<>();
        response.put(AppConstants.SUCCESS, true);

        if (requestId == null) {
            response.put(AppConstants.STATUS, AppConstants.STATUS_APPROVED);
            response.put(AppConstants.TITLE, AppConstants.TITLE_APPROVED);
            response.put(AppConstants.MESSAGE, AppConstants.MSG_APPROVED);
            response.put(AppConstants.DESCRIPTION, AppConstants.DESC_APPROVED);
            response.put(AppConstants.NEXT_STEPS, AppConstants.NEXT_APPROVED);

            return ResponseEntity.ok(response);
        }

        response.put(AppConstants.STATUS, AppConstants.STATUS_PENDING);
        response.put(AppConstants.REQUEST_ID, requestId.toString());
        response.put(AppConstants.TITLE, AppConstants.TITLE_PENDING);
        response.put(AppConstants.MESSAGE, AppConstants.MSG_PENDING);
        response.put(AppConstants.DESCRIPTION, AppConstants.DESC_PENDING);
        response.put(AppConstants.NEXT_STEPS, AppConstants.NEXT_PENDING);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/account/credit-cards/status")
    public CreditCardResponse getStatus(
            @AuthenticationPrincipal AuthUser user
    ) {
        return service.getCreditCardSummary(user.getCustomerId());
    }


    @GetMapping("/admin/credit-cards/pending")
    public ResponseEntity<Map<String, Object>> pending(
            @AuthenticationPrincipal AuthUser user
    ) {
        ensureAdmin(user);

        List<CreditCardRequest> pending = service.getPendingRequests();

        Map<String, Object> response = new HashMap<>();
        response.put(AppConstants.SUCCESS, true);
        response.put(AppConstants.COUNT, pending.size());
        response.put(AppConstants.REQUESTS, pending);

        return ResponseEntity.ok(response);
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