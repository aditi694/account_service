package com.bank.account_service.controller;

import com.bank.account_service.dto.transaction.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.TransactionFacadeService;
import com.bank.account_service.service.TransactionHistoryService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/transactions")
public class TransactionController {

    private final TransactionFacadeService transactionService;
    private final TransactionHistoryService historyService;

    public TransactionController(
            TransactionFacadeService transactionService,
            TransactionHistoryService historyService
    ) {
        this.transactionService = transactionService;
        this.historyService = historyService;
    }

    /* ================= DEBIT ================= */

    @PostMapping("/debit")
    public DebitResponse debit(@RequestBody DebitRequest request) {
        AuthUser user = getUser();
        return transactionService.debit(user.getCustomerId(), request);
    }

    /* ================= CREDIT ================= */

    @PostMapping("/credit")
    public CreditResponse credit(@RequestBody CreditRequest request) {
        AuthUser user = getUser();
        return transactionService.credit(user.getCustomerId(), request);
    }

    /* ================= TRANSACTION HISTORY ================= */

    @GetMapping
    public TransactionHistoryResponse getTransactions(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "1") int page
    ) {
        AuthUser user = getUser();
        return historyService.getTransactions(user.getAccountId(), limit, page);
    }

    /* ================= COMMON ================= */

    private AuthUser getUser() {
        return (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
