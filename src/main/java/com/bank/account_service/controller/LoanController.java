package com.bank.account_service.controller;

import com.bank.account_service.dto.loan.*;
import com.bank.account_service.entity.Loan;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;

    /* ================= CUSTOMER ================= */

    @PostMapping("/account/loans/request")
    public ResponseEntity<LoanRequestResponse> requestLoan(
            @RequestBody IssueLoanRequest request
    ) {
        AuthUser user = getUser();
        return ResponseEntity.ok(
                service.requestLoan(user.getAccountId(), request)
        );
    }

    @GetMapping("/account/loans")
    public ResponseEntity<List<LoanResponse>> myLoans() {
        AuthUser user = getUser();
        return ResponseEntity.ok(
                service.getLoans(user.getCustomerId())
        );
    }

    /* ================= ADMIN ================= */

    @GetMapping("/admin/loans/pending")
    public ResponseEntity<?> getPendingLoans() {
        ensureAdmin();

        List<Loan> pendingLoans = service.getPendingLoans();

        if (pendingLoans.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "No pending loan applications",
                            "description", "All loan requests have already been processed",
                            "count", 0,
                            "loans", pendingLoans
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", pendingLoans.size() + " loan application(s) pending approval",
                        "description", "Review and approve or reject loan applications",
                        "count", pendingLoans.size(),
                        "loans", pendingLoans
                )
        );
    }


    @PostMapping("/admin/loans/{loanId}/approve")
    public ResponseEntity<LoanApprovalResponse> approve(
            @PathVariable String loanId
    ) {
        ensureAdmin();
        return ResponseEntity.ok(
                service.approveLoan(loanId)
        );
    }

    @PostMapping("/admin/loans/{loanId}/reject")
    public ResponseEntity<LoanApprovalResponse> reject(
            @PathVariable String loanId
    ) {
        ensureAdmin();
        return ResponseEntity.ok(
                service.rejectLoan(loanId)
        );
    }

    /* ================= SECURITY HELPERS ================= */

    private AuthUser getUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.unauthorized();
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthUser)) {
            throw BusinessException.unauthorized();
        }

        return (AuthUser) principal;
    }

    private void ensureAdmin() {
        AuthUser user = getUser();

        if (!user.isAdmin()) {
            throw BusinessException.forbidden("Admin access required");
        }
    }
}
