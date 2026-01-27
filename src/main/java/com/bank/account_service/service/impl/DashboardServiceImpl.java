package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.AccountDashboardResponse;
import com.bank.account_service.dto.card.CreditCardRequest;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.dto.client.BankBranchDto;
import com.bank.account_service.dto.client.CustomerClient;
import com.bank.account_service.dto.client.CustomerSummary;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.dto.response.*;
import com.bank.account_service.entity.*;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.enums.InsuranceStatus;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AccountRepository accountRepo;
    private final DebitCardRepository debitRepo;
    private final CreditCardRepository creditCardRepo;
    private final CreditCardRequestRepository requestRepo;
    private final LoanRepository loanRepo;
    private final InsuranceRepository insuranceRepo;
    private final CustomerClient customerClient;


    @Override
    public AccountDashboardResponse getDashboard(AuthUser user) {

        log.info("Building dashboard for customer: {}", user.getCustomerId());

        Account account = accountRepo.findById(user.getAccountId())
                .orElseThrow(BusinessException::accountNotFound);

        CustomerSummary customer = fetchCustomerSummary(user.getCustomerId());
        BankBranchDto bankBranch = fetchBankBranch(account.getIfscCode());

        return AccountDashboardResponse.builder()
                .accountId(account.getId())
                .customerId(user.getCustomerId())
                .customerName(customer != null ? customer.getFullName() : "N/A") // ✅ CUSTOMER NAME
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .bankBranch(buildBankBranchDetails(bankBranch)) // ✅ FIXED BANK BRANCH
                .debitCard(getDebitCardStatus(account.getAccountNumber()))
                .creditCard(getCreditCardStatus(user.getCustomerId())) // ✅ FIXED CREDIT CARD
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
                .map(this::mapDebitCard)
                .orElse(DebitCardResponse.builder()
                        .cardNumber("Not Issued")
                        .expiry("N/A")
                        .dailyLimit(0)
                        .status("NOT_ISSUED")
                        .message("Contact support to issue debit card")
                        .build()
                );
    }

    private DebitCardResponse mapDebitCard(DebitCard card) {
        return DebitCardResponse.builder()
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .expiry(card.getExpiryDate() != null ? card.getExpiryDate().toString() : "N/A")
                .dailyLimit(card.getDailyLimit())
                .status(card.getStatus().name())
                .message("Your debit card is active")
                .build();
    }


    private CreditCardResponse getCreditCardStatus(UUID customerId) {

        Optional<CreditCard> activeCard = creditCardRepo
                .findByCustomerId(customerId)
                .stream()
                .filter(card -> card.getStatus() == CardStatus.ACTIVE)
                .findFirst();

        if (activeCard.isPresent()) {
            CreditCard card = activeCard.get();
            return CreditCardResponse.builder()
                    .cardNumber(maskCardNumber(card.getCardNumber()))
                    .creditLimit(card.getCreditLimit())
                    .availableCredit(card.getAvailableCredit())
                    .outstanding(card.getOutstandingAmount())
                    .status("ACTIVE")
                    .message("Your credit card is active and ready to use")
                    .build();
        }

        Optional<CreditCardRequest> latestRequest = requestRepo
                .findTopByCustomerIdOrderByRequestedAtDesc(customerId);

        if (latestRequest.isPresent()) {
            CreditCardRequest req = latestRequest.get();

            return switch (req.getStatus()) {
                case PENDING -> CreditCardResponse.builder()
                        .status("PENDING_APPROVAL")
                        .message("Your credit card application is under review")
                        .build();

                case REJECTED -> CreditCardResponse.builder()
                        .status("REJECTED")
                        .message("Application rejected. Reason: " +
                                (req.getRejectionReason() != null ? req.getRejectionReason() : "Not specified"))
                        .build();

                default -> buildNotAppliedResponse();
            };
        }

        return buildNotAppliedResponse();
    }

    private CreditCardResponse buildNotAppliedResponse() {
        return CreditCardResponse.builder()
                .status("NOT_APPLIED")
                .message("Apply for a credit card to enjoy exclusive benefits and rewards")
                .build();
    }

    private List<LoanResponse> getCustomerLoans(UUID customerId) {
        return loanRepo.findByAccount_CustomerId(customerId)
                .stream()
                .map(this::mapLoan)
                .toList();
    }

    private LoanResponse mapLoan(Loan loan) {
        String statusMessage = buildLoanStatusMessage(loan.getStatus());

        return LoanResponse.builder()
                .loanId(loan.getLoanId())
                .loanType(loan.getLoanType() != null ? loan.getLoanType().name() : "UNKNOWN")
                .loanAmount(loan.getAmount() != null ? loan.getAmount().doubleValue() : 0.0)
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .emiAmount(loan.getEmiAmount())
                .outstandingAmount(loan.getOutstandingAmount())
                .status(loan.getStatus())
                .statusMessage(statusMessage)
                .build();
    }

    private String buildLoanStatusMessage(LoanStatus status) {
        return switch (status) {
            case ACTIVE -> "Your loan is active. Pay EMI on time to maintain good credit score";
            case REQUESTED -> "Your loan application is under admin review. You'll be notified soon";
            case REJECTED -> "Your loan application was rejected. Please contact support";
            case CLOSED -> "Congratulations! Your loan has been successfully closed";
            case DEFAULTED -> "Please contact support immediately regarding your loan";
        };
    }

    private List<InsuranceResponse> getCustomerInsurances(UUID customerId) {
        return insuranceRepo.findByAccount_CustomerId(customerId)
                .stream()
                .map(this::mapInsurance)
                .toList();
    }

    private InsuranceResponse mapInsurance(Insurance ins) {
        String statusMessage = buildInsuranceStatusMessage(ins.getStatus());

        return InsuranceResponse.builder()
                .policyNumber(ins.getInsuranceId())
                .insuranceType(ins.getInsuranceType() != null ? ins.getInsuranceType().name() : "UNKNOWN")
                .coverageAmount(ins.getCoverageAmount() != null ? ins.getCoverageAmount().doubleValue() : 0.0)
                .premiumAmount(ins.getPremiumAmount())
                .startDate(ins.getStartDate())
                .endDate(ins.getEndDate())
                .status(ins.getStatus())
                .statusMessage(statusMessage)
                .build();
    }

    private String buildInsuranceStatusMessage(InsuranceStatus status) {
        return switch (status) {
            case ACTIVE -> "Your insurance policy is active and providing coverage";
            case REQUESTED -> "Your insurance request is under review";
            case EXPIRED -> "Your policy has expired. Please renew to continue coverage";
            case REJECTED -> "null";
            case CANCELLED -> "This insurance policy has been cancelled";
        };
    }


    private CustomerSummary fetchCustomerSummary(UUID customerId) {
        try {
            return customerClient.getCustomer(customerId);
        } catch (Exception e) {
            log.warn("Failed to fetch customer summary", e);
            return null;
        }
    }

    private BankBranchDto fetchBankBranch(String ifscCode) {
        try {
            if (ifscCode != null && !ifscCode.isBlank()) {
                return customerClient.getBankBranch(ifscCode);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch bank branch for IFSC: {}", ifscCode, e);
        }
        return null;
    }

    private AccountDashboardResponse.BankBranchDetails buildBankBranchDetails(BankBranchDto dto) {
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
                .ifscCode(dto.ifscCode())
                .bankName(dto.bankName())
                .branchName(dto.branchName())
                .city(dto.city())
                .address(dto.address())
                .build();
    }


    private LimitsResponse buildLimits() {
        return LimitsResponse.builder()
                .dailyTransactionLimit(100000)
                .perTransactionLimit(50000)
                .build();
    }

    private NomineeResponse buildNominee(CustomerSummary customer) {
        if (customer != null && customer.getNomineeName() != null) {
            return NomineeResponse.builder()
                    .name(customer.getNomineeName())
                    .relation(customer.getNomineeRelation())
                    .build();
        }
        return NomineeResponse.builder()
                .name("Not Set")
                .relation("N/A")
                .build();
    }

    private KycStatusResponse buildKycStatus(CustomerSummary customer) {
        if (customer != null) {
            boolean verified = "APPROVED".equals(customer.getKycStatus());
            return KycStatusResponse.builder()
                    .verified(verified)
                    .status(customer.getKycStatus())
                    .build();
        }
        return KycStatusResponse.builder()
                .verified(false)
                .status("PENDING")
                .build();
    }

    private List<LinkedAccountResponse> buildLinkedAccounts(List<Account> accounts) {
        return accounts.stream()
                .map(acc -> LinkedAccountResponse.builder()
                        .accountNumber(acc.getAccountNumber())
                        .accountType(acc.getAccountType().name())
                        .balance(acc.getBalance().doubleValue())
                        .primary(acc.isPrimaryAccount())
                        .build())
                .toList();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}