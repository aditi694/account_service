package com.bank.account_service.controller;

import com.bank.account_service.dto.loan.*;
import com.bank.account_service.dto.auth.BaseResponse;
import com.bank.account_service.entity.Loan;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.LoanService;
import com.bank.account_service.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;

    @PostMapping("/account/loans/request")
    public BaseResponse<LoanRequestResponse> requestLoan(
            @Valid @RequestBody IssueLoanRequest request
    ) {

        UUID accountId = SecurityUtil.getCurrentAccountId();

        LoanRequestResponse data = service.requestLoan(accountId, request);

//        String message = data.getStatus().equals("ACTIVE")
//                ? "Loan approved successfully"
//                : "Loan request submitted";
        String message = data.getStatus() == LoanStatus.ACTIVE
                ? "Loan approved successfully"
                : "Loan request submitted";


        return new BaseResponse<>(
                data,
                message,
                AppConstants.SUCCESS_CODE
        );
    }

    @GetMapping("/account/loans")
    public BaseResponse<List<LoanResponse>> myLoans() {

        UUID customerId = SecurityUtil.getCurrentCustomerId();

        List<LoanResponse> data = service.getLoans(customerId);

        return new BaseResponse<>(
                data,
                AppConstants.SUCCESS_MSG,
                AppConstants.SUCCESS_CODE
        );
    }

    @GetMapping("/admin/loans/pending")
    public BaseResponse<Map<String, Object>> pendingLoans() {

        ensureAdmin();

        List<Loan> loans = service.getPendingLoans();

        Map<String, Object> data = Map.of(
                "success", true,
                "count", loans.size(),
                "loans", loans
        );

        return new BaseResponse<>(
                data,
                AppConstants.SUCCESS_MSG,
                AppConstants.SUCCESS_CODE
        );
    }

    @PostMapping("/admin/loans/{loanId}/approve")
    public BaseResponse<LoanApprovalResponse> approve(
            @PathVariable String loanId
    ) {

        ensureAdmin();

        LoanApprovalResponse data = service.approveLoan(loanId);

        return new BaseResponse<>(
                data,
                AppConstants.LOAN_APPROVED,
                AppConstants.SUCCESS_CODE
        );
    }

    @PostMapping("/admin/loans/{loanId}/reject")
    public BaseResponse<LoanApprovalResponse> reject(
            @PathVariable String loanId
    ) {

        ensureAdmin();

        LoanApprovalResponse data = service.rejectLoan(loanId);

        return new BaseResponse<>(
                data,
                "Loan rejected",
                AppConstants.SUCCESS_CODE
        );
    }

    private void ensureAdmin() {
        if (!SecurityUtil.getCurrentUser().isAdmin()) {
            throw BusinessException.forbidden("Admin access required");
        }
    }
}
