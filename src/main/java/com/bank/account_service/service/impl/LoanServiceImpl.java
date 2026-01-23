package com.bank.account_service.service.impl;

import com.bank.account_service.dto.loan.IssueLoanRequest;
import com.bank.account_service.dto.loan.LoanApprovalResponse;
import com.bank.account_service.dto.loan.LoanRequestResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Loan;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.LoanRepository;
import com.bank.account_service.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;

    @Override
    public LoanRequestResponse requestLoan(UUID accountId, IssueLoanRequest request) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        boolean autoApprove = request.getAmount().doubleValue() <= 50_000;

        LoanStatus status = autoApprove
                ? LoanStatus.ACTIVE
                : LoanStatus.REQUESTED;

        Loan loan = Loan.builder()
                .loanId("LN-" + System.currentTimeMillis())
                .account(account)
                .loanType(request.getLoanType())
                .amount(request.getAmount())
                .interestRate(12.0)
                .tenureMonths(12)
                .emiAmount(request.getAmount().doubleValue() / 12)
                .outstandingAmount(request.getAmount().doubleValue())
                .status(status)
                .build();

        loanRepository.save(loan);

        return LoanRequestResponse.builder()
                .loanId(loan.getLoanId())
                .status(status)
                .message(
                        autoApprove
                                ? "Loan approved and activated automatically"
                                : "Loan request submitted for admin approval"
                )
                .build();
    }

    // ---------------- ADMIN ----------------

    @Override
    @Transactional
    public LoanApprovalResponse approveLoan(String loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> BusinessException.badRequest("Loan not found"));

        if (loan.getStatus() != LoanStatus.REQUESTED) {
            throw BusinessException.badRequest("Loan is not pending approval");
        }

        loan.setStatus(LoanStatus.ACTIVE);

        return LoanApprovalResponse.builder()
                .loanId(loanId)
                .previousStatus(LoanStatus.REQUESTED)
                .currentStatus(LoanStatus.ACTIVE)
                .message("Loan approved successfully")
                .build();
    }

    @Override
    @Transactional
    public LoanApprovalResponse rejectLoan(String loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(BusinessException::loanNotFound);

        if (loan.getStatus() != LoanStatus.REQUESTED) {
            throw BusinessException.badRequest("Loan is not pending approval");
        }

        loan.setStatus(LoanStatus.REJECTED);

        return LoanApprovalResponse.builder()
                .loanId(loanId)
                .previousStatus(LoanStatus.REQUESTED)
                .currentStatus(LoanStatus.REJECTED)
                .message("Loan rejected successfully")
                .build();
    }

    // ---------------- CUSTOMER ----------------

    @Override
    public List<LoanResponse> getLoans(UUID customerId) {
        return loanRepository.findByAccount_CustomerId(customerId)
                .stream()
                .map(this::mapLoan)
                .toList();
    }

    private LoanResponse mapLoan(Loan loan) {
        return LoanResponse.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType().name())
                .loanAmount(loan.getAmount().doubleValue())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .emiAmount(loan.getEmiAmount())
                .outstandingAmount(loan.getOutstandingAmount())
                .status(loan.getStatus())
                .build();
    }
}
