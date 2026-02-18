package com.bank.account_service.service;

import com.bank.account_service.dto.loan.IssueLoanRequest;
import com.bank.account_service.dto.loan.response.LoanApprovalResponse;
import com.bank.account_service.dto.loan.response.LoanRequestResponse;
import com.bank.account_service.dto.loan.response.LoanResponse;
import com.bank.account_service.entity.Loan;

import java.util.List;
import java.util.UUID;

public interface LoanService {

    LoanRequestResponse requestLoan(UUID accountId, IssueLoanRequest request);

    LoanApprovalResponse approveLoan(String loanId);

    LoanApprovalResponse rejectLoan(String loanId);

    List<LoanResponse> getLoans(UUID customerId);

    List<Loan> getPendingLoans();
}