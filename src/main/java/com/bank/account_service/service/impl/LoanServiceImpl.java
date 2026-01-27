package com.bank.account_service.service.impl;

import com.bank.account_service.dto.loan.IssueLoanRequest;
import com.bank.account_service.dto.loan.LoanApprovalResponse;
import com.bank.account_service.dto.loan.LoanRequestResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Loan;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.enums.LoanType;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.LoanRepository;
import com.bank.account_service.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;

    private static final double AUTO_APPROVAL_LIMIT = 50000.0;

    @Override
    public LoanRequestResponse requestLoan(UUID accountId, IssueLoanRequest request) {

        log.info("Loan request received for account: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        double interestRate = calculateInterestRate(request.getLoanType());
        int tenureMonths = calculateTenure(request.getLoanType());
        double emiAmount = calculateEMI(
                request.getAmount().doubleValue(),
                interestRate,
                tenureMonths
        );

        boolean autoApprove = request.getAmount().doubleValue() <= AUTO_APPROVAL_LIMIT;
        LoanStatus status = autoApprove ? LoanStatus.ACTIVE : LoanStatus.REQUESTED;

        Loan loan = Loan.builder()
                .loanId("LN-" + System.currentTimeMillis())
                .account(account)
                .loanType(request.getLoanType())
                .amount(request.getAmount())
                .interestRate(interestRate)
                .tenureMonths(tenureMonths)
                .emiAmount(emiAmount)
                .outstandingAmount(request.getAmount().doubleValue())
                .status(status)
                .build();

        loanRepository.save(loan);

        log.info("Loan {} created with status: {}", loan.getLoanId(), status);

        String message = autoApprove
                ? "Your loan of ₹" + request.getAmount() + " has been approved automatically. EMI: ₹" + emiAmount
                : "Your loan application for ₹" + request.getAmount() + " has been submitted for review. You will be notified once approved.";

        return LoanRequestResponse.builder()
                .loanId(loan.getLoanId())
                .status(status)
                .message(message)
                .build();
    }

    @Override
    public LoanApprovalResponse approveLoan(String loanId) {

        log.info("Admin approving loan: {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(BusinessException::loanNotFound);

        if (loan.getStatus() != LoanStatus.REQUESTED) {
            throw BusinessException.badRequest(
                    "Loan is not in pending status. Current status: " + loan.getStatus()
            );
        }

        LoanStatus previousStatus = loan.getStatus();
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.save(loan);

        log.info("Loan {} approved successfully", loanId);

        return LoanApprovalResponse.builder()
                .loanId(loanId)
                .previousStatus(previousStatus)
                .currentStatus(LoanStatus.ACTIVE)
                .message("Loan of ₹" + loan.getAmount() + " approved successfully. EMI: ₹" + loan.getEmiAmount())
                .build();
    }

    @Override
    public LoanApprovalResponse rejectLoan(String loanId) {

        log.info("Admin rejecting loan: {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(BusinessException::loanNotFound);

        if (loan.getStatus() != LoanStatus.REQUESTED) {
            throw BusinessException.badRequest(
                    "Loan is not in pending status. Current status: " + loan.getStatus()
            );
        }

        LoanStatus previousStatus = loan.getStatus();
        loan.setStatus(LoanStatus.REJECTED);
        loanRepository.save(loan);

        log.info("Loan {} rejected successfully", loanId);

        return LoanApprovalResponse.builder()
                .loanId(loanId)
                .previousStatus(previousStatus)
                .currentStatus(LoanStatus.REJECTED)
                .message("Loan application has been rejected")
                .build();
    }

    @Override
    public List<LoanResponse> getLoans(UUID customerId) {
        return loanRepository.findByAccount_CustomerId(customerId)
                .stream()
                .map(this::mapLoan)
                .toList();
    }

    @Override
    public List<Loan> getPendingLoans() {
        return loanRepository.findByStatus(LoanStatus.REQUESTED);
    }

    // Helper methods

    private double calculateInterestRate(LoanType loanType) {
        return switch (loanType) {
            case PERSONAL -> 12.0;
            case HOME -> 8.5;
            case CAR -> 9.0;
            case EDUCATION -> 10.0;
            case BUSINESS -> 11.5;
        };
    }

    private int calculateTenure(LoanType loanType) {
        return switch (loanType) {
            case PERSONAL -> 24; // 2 years
            case HOME -> 240;    // 20 years
            case CAR -> 60;      // 5 years
            case EDUCATION -> 84; // 7 years
            case BUSINESS -> 120; // 10 years
        };
    }

    private double calculateEMI(double principal, double annualRate, int months) {
        double monthlyRate = annualRate / 12 / 100;
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1);

        return BigDecimal.valueOf(emi)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private LoanResponse mapLoan(Loan loan) {
        return LoanResponse.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType() != null ? loan.getLoanType().name() : "UNKNOWN")
                .loanAmount(loan.getAmount() != null ? loan.getAmount().doubleValue() : 0.0)
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .emiAmount(loan.getEmiAmount())
                .outstandingAmount(loan.getOutstandingAmount())
                .status(loan.getStatus())
                .build();
    }
}