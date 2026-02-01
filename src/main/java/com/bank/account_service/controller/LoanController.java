package com.bank.account_service.controller;

import com.bank.account_service.dto.loan.*;
import com.bank.account_service.entity.Loan;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;

    @PostMapping("/account/loans/request")
    public LoanRequestResponse requestLoan(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody IssueLoanRequest request
    ) {
        return service.requestLoan(user.getAccountId(), request);
    }

    @GetMapping("/account/loans")
    public List<LoanResponse> myLoans(
            @AuthenticationPrincipal AuthUser user
    ) {
        return service.getLoans(user.getCustomerId());
    }

    @GetMapping("/admin/loans/pending")
    public Map<String, Object> pendingLoans(
            @AuthenticationPrincipal AuthUser user
    ) {
        ensureAdmin(user);
        List<Loan> loans = service.getPendingLoans();

        return Map.of(
                "success", true,
                "count", loans.size(),
                "loans", loans
        );
    }

    @PostMapping("/admin/loans/{loanId}/approve")
    public LoanApprovalResponse approve(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable String loanId
    ) {
        ensureAdmin(user);
        return service.approveLoan(loanId);
    }

    @PostMapping("/admin/loans/{loanId}/reject")
    public LoanApprovalResponse reject(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable String loanId
    ) {
        ensureAdmin(user);
        return service.rejectLoan(loanId);
    }

    private void ensureAdmin(AuthUser user) {
        if (!user.isAdmin()) {
            throw BusinessException.forbidden("Admin access required");
        }
    }
}