package com.bank.account_service.controller;

import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.dto.auth.BaseResponse;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CardService;
import com.bank.account_service.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DebitCardController {

    private final CardService service;

    @GetMapping("/account/cards/debit")
    public BaseResponse<DebitCardResponse> myDebitCard(@AuthenticationPrincipal AuthUser user) {
        DebitCardResponse data = service.getDebitCard(user.getAccountId());
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @PostMapping("/admin/cards/debit/{accountId}/block")
    public BaseResponse<Void> block(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID accountId
    ) {
        ensureAdmin(user);
        service.blockDebitCard(accountId);
        return new BaseResponse<>(null, "Debit card blocked", AppConstants.SUCCESS_CODE);
    }

    @PostMapping("/admin/cards/debit/{accountId}/unblock")
    public BaseResponse<Void> unblock(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID accountId
    ) {
        ensureAdmin(user);
        service.unblockDebitCard(accountId);
        return new BaseResponse<>(null, "Debit card unblocked", AppConstants.SUCCESS_CODE);
    }

    private void ensureAdmin(AuthUser user) {
        if (!user.isAdmin()) {
            throw BusinessException.forbidden("Admin access required");
        }
    }
}