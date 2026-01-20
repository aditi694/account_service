package com.bank.account_service.controller;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService service;

    // CUSTOMER
    @GetMapping("/account/credit-cards")
    public List<CreditCardResponse> myCards() {
        AuthUser user = getUser();
        return service.getCards(user.getCustomerId());
    }

    @PostMapping("/admin/credit-cards/issue/{customerId}")
    public CreditCardIssueResponse issue(
            @PathVariable UUID customerId,
            @RequestParam double limit
    ) {
        return service.issueCard(customerId, limit);
    }

    private AuthUser getUser() {
        return (AuthUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
