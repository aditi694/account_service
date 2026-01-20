package com.bank.account_service.controller;

import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DebitCardController {

    private final CardService service;

    // CUSTOMER
    @GetMapping("/account/cards/debit")
    public DebitCardResponse myDebitCard() {
        AuthUser user = getUser();
        return service.getDebitCard(user.getAccountId());
    }

    // ADMIN
    @PostMapping("/admin/cards/debit/{accountId}/issue")
    public void issue(@PathVariable UUID accountId) {
        service.issueDebitCard(accountId);
    }

    @PostMapping("/admin/cards/debit/{accountId}/block")
    public void block(@PathVariable UUID accountId) {
        service.blockDebitCard(accountId);
    }

    @PostMapping("/admin/cards/debit/{accountId}/unblock")
    public void unblock(@PathVariable UUID accountId) {
        service.unblockDebitCard(accountId);
    }

    private AuthUser getUser() {
        return (AuthUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
