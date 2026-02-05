package com.bank.account_service.service.impl;

import static com.bank.account_service.util.AppConstants.*;

import com.bank.account_service.dto.account.AccountDashboardResponse;
import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.dto.client.BankBranchDto;
import com.bank.account_service.dto.client.CustomerClient;
import com.bank.account_service.dto.client.CustomerSnapshot;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.entity.*;
import com.bank.account_service.enums.InsuranceStatus;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CreditCardService;
import com.bank.account_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AccountRepository accountRepo;
    private final DebitCardRepository debitRepo;
    private final LoanRepository loanRepo;
    private final InsuranceRepository insuranceRepo;
    private final CustomerClient customerClient;
    private final CreditCardService creditCardService;

    @Override
    public AccountDashboardResponse getDashboard(AuthUser user) {

        Account account = accountRepo.findById(user.getAccountId())
                .orElseThrow(BusinessException::accountNotFound);

        CustomerSnapshot customer = fetchCustomerSummary(user.getCustomerId());
        BankBranchDto bankBranch = fetchBankBranch(account.getIfscCode());

        return AccountDashboardResponse.builder()
                .accountId(account.getId())
                .customerId(user.getCustomerId())
                .customerName(customer != null ? customer.getFullName() : "N/A")
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .accountTypeDescription(getAccountTypeDescription(account.getAccountType()))  // ✅
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .statusMessage(getAccountStatusMessage(account.getStatus()))  // ✅

                .bankBranch(buildBankBranch(bankBranch))
                .debitCard(getDebitCardStatus(account.getAccountNumber()))
                .creditCard(creditCardService.getCreditCardSummary(user.getCustomerId()))

                .loans(getCustomerLoans(user.getCustomerId()))
                .insurances(getCustomerInsurances(user.getCustomerId()))

                .limits(buildLimits())
                .nominee(buildNominee(customer))
                .kyc(buildKycStatus(customer))

                .linkedAccounts(buildLinkedAccounts(
                        accountRepo.findByCustomerId(user.getCustomerId())
                ))
                .build();
    }


    private DebitCardResponse getDebitCardStatus(String accountNumber) {
        return debitRepo.findByAccountNumber(accountNumber)
                .map(card -> DebitCardResponse.builder()
                        .cardNumber(maskCardNumber(card.getCardNumber()))
                        .expiry(card.getExpiryDate() != null
                                ? card.getExpiryDate().toString()
                                : "N/A")
                        .dailyLimit(card.getDailyLimit())
                        .status(card.getStatus().name())
                        .message(getCardStatusMessage(card.getStatus()))
                        .build()
                )
                .orElse(DebitCardResponse.builder()
                        .cardNumber("Not Issued")
                        .expiry("N/A")
                        .dailyLimit(0)
                        .status("NOT_ISSUED")
                        .message(CARD_NOT_ISSUED_MSG)
                        .build());
    }


    private List<LoanResponse> getCustomerLoans(UUID customerId) {
        return loanRepo.findByAccount_CustomerId(customerId)
                .stream()
                .map(this::mapLoan)
                .toList();
    }

    private LoanResponse mapLoan(Loan loan) {
        LoanStatus status = loan.getStatus();

        if (status == LoanStatus.REQUESTED || status == LoanStatus.REJECTED) {
            return LoanResponse.builder()
                    .loanId(loan.getLoanId())
                    .loanType(loan.getLoanType().name())
                    .status(status)
                    .statusMessage(getLoanStatusMessage(status))
                    .build();
        }

        return LoanResponse.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType().name())
                .loanAmount(loan.getAmount().doubleValue())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .emiAmount(loan.getEmiAmount())
                .outstandingAmount(loan.getOutstandingAmount())
                .status(status)
                .statusMessage(getLoanStatusMessage(status))
                .build();
    }

    private String getLoanStatusMessage(LoanStatus status) {
        return switch (status) {
            case ACTIVE -> LOAN_ACTIVE_MSG;
            case REQUESTED -> LOAN_REQUESTED_MSG;
            case REJECTED -> LOAN_REJECTED_MSG;
            case CLOSED -> LOAN_CLOSED_MSG;
            case DEFAULTED -> LOAN_DEFAULTED_MSG;
        };
    }


    private List<InsuranceResponse> getCustomerInsurances(UUID customerId) {
        return insuranceRepo.findByAccount_CustomerId(customerId)
                .stream()
                .map(this::mapInsurance)
                .toList();
    }

    private InsuranceResponse mapInsurance(Insurance ins) {
        return InsuranceResponse.builder()
                .policyNumber(ins.getInsuranceId())
                .insuranceType(ins.getInsuranceType().name())
                .coverageAmount(ins.getCoverageAmount().doubleValue())
                .premiumAmount(ins.getPremiumAmount())
                .startDate(ins.getStartDate())
                .endDate(ins.getEndDate())
                .status(ins.getStatus())
                .statusMessage(getInsuranceStatusMessage(ins.getStatus()))
                .build();
    }

    private String getInsuranceStatusMessage(InsuranceStatus status) {
        return switch (status) {
            case ACTIVE -> INSURANCE_ACTIVE_MSG;
            case REQUESTED -> INSURANCE_REQUESTED_MSG;
            case REJECTED -> INSURANCE_REJECTED_MSG;
            case CANCELLED -> INSURANCE_CANCELLED_MSG;
            case EXPIRED -> INSURANCE_EXPIRED_MSG;
        };
    }

    private String getAccountStatusMessage(com.bank.account_service.enums.AccountStatus status) {
        return switch (status) {
            case ACTIVE -> ACCOUNT_ACTIVE_MSG;
            case DORMANT -> ACCOUNT_DORMANT_MSG;
            case BLOCKED -> ACCOUNT_BLOCKED_MSG;
            case CLOSED -> ACCOUNT_CLOSED_MSG;
        };
    }

    private String getAccountTypeDescription(com.bank.account_service.enums.AccountType type) {
        return switch (type) {
            case SAVINGS -> ACC_TYPE_SAVINGS_DESC;
            case CURRENT -> ACC_TYPE_CURRENT_DESC;
            case FIXED_DEPOSIT -> ACC_TYPE_FD_DESC;
            case RECURRING_DEPOSIT -> ACC_TYPE_RD_DESC;
        };
    }

    private String getCardStatusMessage(com.bank.account_service.enums.CardStatus status) {
        return switch (status) {
            case ACTIVE -> CARD_ACTIVE_MSG;
            case BLOCKED -> CARD_BLOCKED_MSG;
            case EXPIRED -> CARD_EXPIRED_MSG;
            default -> CARD_NOT_ISSUED_MSG;
        };
    }

    private CustomerSnapshot fetchCustomerSummary(UUID customerId) {
        try {
            return customerClient.getCustomer(customerId);
        } catch (Exception e) {
            log.warn("Failed to fetch customer summary", e);
            return null;
        }
    }

    private AccountDashboardResponse.Limits buildLimits() {
        return AccountDashboardResponse.Limits.builder()
                .dailyTransactionLimit(100_000)
                .perTransactionLimit(50_000)
                .build();
    }

    private AccountDashboardResponse.Nominee buildNominee(CustomerSnapshot customer) {
        if (customer != null && customer.getNomineeName() != null) {
            return AccountDashboardResponse.Nominee.builder()
                    .name(customer.getNomineeName())
                    .relation(customer.getNomineeRelation())
                    .build();
        }
        return AccountDashboardResponse.Nominee.builder()
                .name("Not Set")
                .relation("N/A")
                .build();
    }

    private AccountDashboardResponse.KycStatus buildKycStatus(CustomerSnapshot customer) {
        if (customer != null) {
            boolean verified = "APPROVED".equals(customer.getKycStatus());
            return AccountDashboardResponse.KycStatus.builder()
                    .verified(verified)
                    .status(customer.getKycStatus())
                    .build();
        }
        return AccountDashboardResponse.KycStatus.builder()
                .verified(false)
                .status("PENDING")
                .build();
    }

    private List<AccountDashboardResponse.LinkedAccount> buildLinkedAccounts(List<Account> accounts) {
        return accounts.stream()
                .map(acc -> AccountDashboardResponse.LinkedAccount.builder()
                        .accountNumber(acc.getAccountNumber())
                        .accountType(acc.getAccountType().name())
                        .balance(acc.getBalance().doubleValue())
                        .primary(acc.isPrimaryAccount())
                        .build())
                .toList();
    }

    private AccountDashboardResponse.BankBranchDetails buildBankBranch(BankBranchDto dto) {
        if (dto == null) {
            return AccountDashboardResponse.BankBranchDetails.builder()
                    .ifscCode("N/A")
                    .bankName("N/A")
                    .branchName("N/A")
                    .city("N/A")
                    .address("N/A")
                    .build();
        }

        return AccountDashboardResponse.BankBranchDetails.builder()
                .ifscCode(dto.getIfscCode())
                .bankName(dto.getBankName())
                .branchName(dto.getBranchName())
                .city(dto.getCity())
                .address(dto.getAddress())
                .build();
    }

    private BankBranchDto fetchBankBranch(String ifscCode) {
        try {
            return customerClient.getBankBranch(ifscCode);
        } catch (Exception e) {
            log.warn("Failed to fetch bank branch for IFSC {}", ifscCode, e);
            return null;
        }
    }


    private String maskCardNumber(String cardNumber) {
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}