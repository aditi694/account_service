package com.bank.account_service.controller;

import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CardService;
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
    public DebitCardResponse myDebitCard(
            @AuthenticationPrincipal AuthUser user
    ) {
        return service.getDebitCard(user.getAccountId());
    }

    @PostMapping("/admin/cards/debit/{accountId}/block")
    public void block(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID accountId
    ) {
        ensureAdmin(user);
        service.blockDebitCard(accountId);
    }

    @PostMapping("/admin/cards/debit/{accountId}/unblock")
    public void unblock(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable UUID accountId
    ) {
        ensureAdmin(user);
        service.unblockDebitCard(accountId);
    }

    private void ensureAdmin(AuthUser user) {
        if (!user.isAdmin()) {
            throw BusinessException.forbidden("Admin access required");
        }
    }
}