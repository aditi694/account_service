package com.bank.account_service.controller;

import com.bank.account_service.dto.loan.IssueLoanRequest;
import com.bank.account_service.dto.loan.LoanApprovalResponse;
import com.bank.account_service.dto.loan.LoanRequestResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.LoanService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping("/account/loans/request")
    public LoanRequestResponse requestLoan(
            @RequestBody IssueLoanRequest request) {

        AuthUser user = (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return service.requestLoan(user.getAccountId(), request);
    }


    @GetMapping("/account/loans")
    public List<LoanResponse> myLoans() {

        AuthUser user = (AuthUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return service.getLoans(user.getCustomerId());
    }

    // 🔐 ADMIN
    @PostMapping("/admin/loans/{loanId}/approve")
    public LoanApprovalResponse approve(@PathVariable String loanId) {
        return service.approveLoan(loanId);
    }

    @PostMapping("/admin/loans/{loanId}/reject")
    public LoanApprovalResponse reject(@PathVariable String loanId) {
        return service.rejectLoan(loanId);
    }
}
