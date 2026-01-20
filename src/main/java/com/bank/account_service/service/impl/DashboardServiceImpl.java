package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.AccountDashboardResponse;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.dto.client.CustomerClient;
import com.bank.account_service.dto.client.CustomerSummary;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.dto.response.*;
import com.bank.account_service.entity.*;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final AccountRepository accountRepo;
    private final DebitCardRepository debitRepo;
    private final LoanRepository loanRepo;
    private final InsuranceRepository insuranceRepo;
    private final CustomerClient customerClient;  // ✅ Add this
    private final CreditCardRepository creditCardRepo;

    public DashboardServiceImpl(
            AccountRepository accountRepo,
            DebitCardRepository debitRepo,
            LoanRepository loanRepo,
            InsuranceRepository insuranceRepo,
            CustomerClient customerClient, CreditCardRepository creditCardRepo  // ✅ Add this
    ) {
        this.accountRepo = accountRepo;
        this.debitRepo = debitRepo;
        this.loanRepo = loanRepo;
        this.insuranceRepo = insuranceRepo;
        this.customerClient = customerClient;
        this.creditCardRepo = creditCardRepo;
    }

    @Override
    public AccountDashboardResponse getDashboard(AuthUser user) {

        System.out.println("=== DASHBOARD REQUEST ===");
        System.out.println("Account ID: " + user.getAccountId());
        System.out.println("Customer ID: " + user.getCustomerId());

        // 1. Find account
        Account account = accountRepo.findById(user.getAccountId())
                .orElseThrow(() -> {
                    System.err.println("Account not found: " + user.getAccountId());
                    return BusinessException.accountNotFound();
                });

        System.out.println("Account found: " + account.getAccountNumber());

        // 2. Fetch customer data from Customer Service
        CustomerSummary customer = null;
        try {
            customer = customerClient.getCustomer(UUID.fromString(user.getCustomerId().toString()));
            System.out.println("Customer data fetched: " + customer.getFullName());
        } catch (Exception e) {
            System.err.println("Failed to fetch customer data: " + e.getMessage());
        }

        // 3. Get debit card
        Optional<DebitCard> debitCard = debitRepo.findByAccountNumber(account.getAccountNumber());
        Optional<CreditCard> creditCard =
                creditCardRepo.findByCustomerId(user.getCustomerId())
                        .stream()
                        .findFirst();

        // 4. Get loans
        List<Loan> loans = loanRepo.findByAccount_CustomerId(user.getCustomerId());

        // 5. Get insurances
        List<Insurance> insurances = insuranceRepo.findByAccount_CustomerId(user.getCustomerId());

        // 6. Get linked accounts (all accounts of same customer)
        List<Account> linkedAccounts = accountRepo.findByCustomerId(user.getCustomerId());

        // 7. Build dashboard response
        return AccountDashboardResponse.builder()
                .accountId(account.getId())
                .customerId(user.getCustomerId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())

                // Cards
                .debitCard(mapDebit(debitCard))
                .creditCard(
                        creditCard.map(c -> CreditCardResponse.builder()
                                .cardNumber(mask(c.getCardNumber()))
                                .creditLimit(c.getCreditLimit())
                                .availableCredit(c.getAvailableCredit())
                                .outstanding(c.getOutstandingAmount())
                                .status(c.getStatus().name())
                                .build()
                        ).orElse(
                                CreditCardResponse.builder()
                                        .cardNumber("Not Issued")
                                        .creditLimit(0)
                                        .availableCredit(0)
                                        .outstanding(0)
                                        .status("NOT_ISSUED")
                                        .build()
                        )
                )



                // Loans & Insurance
                .loans(mapLoans(loans))
                .insurances(mapInsurances(insurances))

                // Limits
                .limits(LimitsResponse.builder()
                        .dailyTransactionLimit(100000)
                        .perTransactionLimit(50000)
                        .build())

                .nominee(
                        customer != null && customer.getNomineeName() != null
                                ? NomineeResponse.builder()
                                .name(customer.getNomineeName())
                                .relation(customer.getNomineeRelation())
                                .build()
                                : NomineeResponse.builder()
                                .name("Not Set")
                                .relation("N/A")
                                .build()
                )


                // KYC - from Customer Service
                .kyc(customer != null
                        ? KycStatusResponse.builder()
                        .verified(customer.getKycStatus().equals("APPROVED"))
                        .status(customer.getKycStatus())
                        .build()
                        : KycStatusResponse.builder()
                        .verified(false)
                        .status("PENDING")
                        .build())

                // Linked Accounts - all accounts of customer
                .linkedAccounts(mapLinkedAccounts(linkedAccounts, account.getAccountNumber()))

                .build();
    }

    /* -------------------- HELPERS -------------------- */

    private DebitCardResponse mapDebit(Optional<DebitCard> card) {
        if (card.isEmpty()) {
            return DebitCardResponse.builder()
                    .cardNumber("Not Issued")
                    .expiry("N/A")
                    .dailyLimit(0)
                    .status("Not Issued")
                    .build();
        }

        DebitCard c = card.get();
        return DebitCardResponse.builder()
                .cardNumber(mask(c.getCardNumber()))
                .expiry(c.getExpiryDate() != null ? c.getExpiryDate().toString() : "N/A")
                .dailyLimit(c.getDailyLimit())
                .status(c.getStatus().name())
                .build();
    }

    private List<LoanResponse> mapLoans(List<Loan> loans) {
        return loans.stream()
                .map(this::mapLoan)
                .toList();
    }

    private LoanResponse mapLoan(Loan loan) {
        return LoanResponse.builder()
                .loanId(loan.getLoanId())
                .loanAmount(
                        loan.getAmount() != null ? loan.getAmount().doubleValue() : 0
                )
                .emiAmount(loan.getEmiAmount())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .outstandingAmount(loan.getOutstandingAmount())
                .loanType(
                        loan.getLoanType() != null ? loan.getLoanType().name() : null
                )
                .status(loan.getStatus())
                .build();
    }


    private List<InsuranceResponse> mapInsurances(List<Insurance> insurances) {
        return insurances.stream()
                .map(this::mapInsurance)
                .toList();
    }

    private InsuranceResponse mapInsurance(Insurance ins) {
        return InsuranceResponse.builder()
                .policyNumber(ins.getInsuranceId())
                .insuranceType(
                        ins.getInsuranceType() != null ? ins.getInsuranceType().name() : null
                )
                .coverageAmount(
                        ins.getCoverageAmount() != null ? ins.getCoverageAmount().doubleValue() : 0
                )
                .premiumAmount(ins.getPremiumAmount())
                .startDate(ins.getStartDate())
                .endDate(ins.getEndDate())
                .status(ins.getStatus())
                .build();
    }


    private List<LinkedAccountResponse> mapLinkedAccounts(List<Account> accounts, String currentAccountNumber) {
        return accounts.stream()
                .map(acc -> LinkedAccountResponse.builder()
                        .accountNumber(acc.getAccountNumber())
                        .accountType(acc.getAccountType().name())
                        .balance(acc.getBalance().doubleValue())
                        .primary(acc.isPrimaryAccount())
                        .build())
                .toList();
    }

    private String mask(String value) {
        if (value == null || value.length() < 4) return "XXXX";
        return "XXXX-XXXX-XXXX-" + value.substring(value.length() - 4);
    }
}