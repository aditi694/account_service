package com.bank.account_service.service;

import com.bank.account_service.dto.loan.IssueLoanRequest;
import com.bank.account_service.dto.loan.LoanApprovalResponse;
import com.bank.account_service.dto.loan.LoanRequestResponse;
import com.bank.account_service.dto.loan.LoanResponse;

import java.util.List;
import java.util.UUID;
public interface LoanService {

    List<LoanResponse> getLoans(UUID customerId);

    LoanRequestResponse requestLoan(UUID accountId, IssueLoanRequest request);

    LoanApprovalResponse approveLoan(String loanId);

    LoanApprovalResponse rejectLoan(String loanId);
}
