package com.bank.account_service.controller;

import com.bank.account_service.dto.auth.BaseResponse;
import com.bank.account_service.dto.card.*;
import com.bank.account_service.entity.CreditCardRequest;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CreditCardService;
import com.bank.account_service.util.AppConstants;
import jakarta.validation.Valid;
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
    public ResponseEntity<BaseResponse<Map<String, Object>>> apply(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody CreditCardApplyRequest request
    ) {
        UUID requestId =
                service.applyCreditCard(user, request.getCardHolderName());


        Map<String, Object> data = new HashMap<>();
        data.put(AppConstants.SUCCESS, true);

        if (requestId == null) {
            data.put(AppConstants.STATUS, AppConstants.STATUS_APPROVED);
            data.put(AppConstants.TITLE, AppConstants.TITLE_APPROVED);
            data.put(AppConstants.MESSAGE, AppConstants.MSG_APPROVED);
            data.put(AppConstants.DESCRIPTION, AppConstants.DESC_APPROVED);
            data.put(AppConstants.NEXT_STEPS, AppConstants.NEXT_APPROVED);

            return ResponseEntity.ok(
                    new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE)
            );
        }

        data.put(AppConstants.STATUS, AppConstants.STATUS_PENDING);
        data.put(AppConstants.REQUEST_ID, requestId.toString());
        data.put(AppConstants.TITLE, AppConstants.TITLE_PENDING);
        data.put(AppConstants.MESSAGE, AppConstants.MSG_PENDING);
        data.put(AppConstants.DESCRIPTION, AppConstants.DESC_PENDING);
        data.put(AppConstants.NEXT_STEPS, AppConstants.NEXT_PENDING);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE));
    }

    @GetMapping("/account/credit-cards/status")
    public BaseResponse<CreditCardResponse> getStatus(
            @AuthenticationPrincipal AuthUser user
    ) {
        CreditCardResponse data =
                service.getCreditCardSummary(user.getCustomerId());

        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @GetMapping("/admin/credit-cards/pending")
    public BaseResponse<Map<String, Object>> pending(
            @AuthenticationPrincipal AuthUser user
    ) {
        ensureAdmin(user);

        List<CreditCardRequest> pending = service.getPendingRequests();

        Map<String, Object> data = new HashMap<>();
        data.put(AppConstants.SUCCESS, true);
        data.put(AppConstants.COUNT, pending.size());
        data.put(AppConstants.REQUESTS, pending);

        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @PostMapping("/admin/credit-cards/approve/{requestId}")
    public BaseResponse<CreditCardIssueResponse> approve(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID requestId
    ) {
        ensureAdmin(user);

        CreditCardIssueResponse data =
                service.approveRequest(requestId);

        return new BaseResponse<>(data, AppConstants.CREDIT_CARD_APPROVED, AppConstants.SUCCESS_CODE);
    }

    @PostMapping("/admin/credit-cards/reject/{requestId}")
    public BaseResponse<Void> reject(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID requestId,
            @RequestParam String reason
    ) {
        ensureAdmin(user);
        service.rejectRequest(requestId, reason);

        return new BaseResponse<>(null, AppConstants.CONFLICT_MSG, AppConstants.SUCCESS_CODE);
    }

    private void ensureAdmin(AuthUser user) {
        if (!user.isAdmin()) {
            throw BusinessException.forbidden(AppConstants.FORBIDDEN_MSG);
        }
    }
}
