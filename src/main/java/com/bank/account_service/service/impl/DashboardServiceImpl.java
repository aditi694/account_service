package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.AccountDashboardResponse;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.dto.client.CustomerClient;
import com.bank.account_service.dto.client.CustomerSummary;
import com.bank.account_service.dto.client.BankBranchDto;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.dto.response.*;
import com.bank.account_service.entity.*;
import com.bank.account_service.enums.CardStatus;
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
    private final CustomerClient customerClient;
    private final CreditCardRepository creditCardRepo;
    private final CreditCardRequestRepository requestRepo;


    public DashboardServiceImpl(
            AccountRepository accountRepo,
            DebitCardRepository debitRepo,
            LoanRepository loanRepo,
            InsuranceRepository insuranceRepo,
            CustomerClient customerClient,
            CreditCardRepository creditCardRepo, CreditCardRequestRepository requestRepo
    ) {
        this.accountRepo = accountRepo;
        this.debitRepo = debitRepo;
        this.loanRepo = loanRepo;
        this.insuranceRepo = insuranceRepo;
        this.customerClient = customerClient;
        this.creditCardRepo = creditCardRepo;
        this.requestRepo = requestRepo;
    }

    @Override
    public AccountDashboardResponse getDashboard(AuthUser user) {

        Account account = accountRepo.findById(user.getAccountId())
                .orElseThrow(BusinessException::accountNotFound);

        CustomerSummary customer = fetchCustomerSummary(user.getCustomerId());


        BankBranchDto bankBranch = fetchBankBranch(account.getIfscCode());

        Optional<DebitCard> debitCard = debitRepo.findByAccountNumber(
                account.getAccountNumber()
        );
        Optional<CreditCard> creditCard = creditCardRepo
                .findByCustomerId(user.getCustomerId())
                .stream()
                .findFirst();

        List<Loan> loans = loanRepo.findByAccount_CustomerId(
                user.getCustomerId()
        );
        List<Insurance> insurances = insuranceRepo.findByAccount_CustomerId(
                user.getCustomerId()
        );

        List<Account> linkedAccounts = accountRepo.findByCustomerId(
                user.getCustomerId()
        );

        return AccountDashboardResponse.builder()
                .accountId(account.getId())
                .customerId(user.getCustomerId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())

                .bankBranch(bankBranch != null
                        ? AccountDashboardResponse.BankBranchDetails.builder()
                        .ifscCode(bankBranch.ifscCode())
                        .bankName(bankBranch.bankName())
                        .branchName(bankBranch.branchName())
                        .city(bankBranch.city())
                        .address(bankBranch.branchName() + ", " + bankBranch.city())
                        .build()
                        : null)

                .debitCard(mapDebit(debitCard))
                .creditCard(mapCredit(creditCard, user.getCustomerId()))

                .loans(mapLoans(loans))
                .insurances(mapInsurances(insurances))

                .limits(LimitsResponse.builder()
                        .dailyTransactionLimit(100000)
                        .perTransactionLimit(50000)
                        .build())

                .nominee(mapNominee(customer))

                .kyc(mapKyc(customer))

                .linkedAccounts(mapLinkedAccounts(
                        linkedAccounts,
                        account.getAccountNumber()
                ))

                .build();
    }

    private CustomerSummary fetchCustomerSummary(UUID customerId) {
        try {
            return customerClient.getCustomer(customerId);
        } catch (Exception e) {
            return null;
        }
    }

    private BankBranchDto fetchBankBranch(String ifscCode) {
        try {
            return customerClient.getBankBranchByIfsc(ifscCode);
        } catch (Exception e) {
            return null;
        }
    }

    private DebitCardResponse mapDebit(Optional<DebitCard> card) {
        if (card.isEmpty()) {
            return DebitCardResponse.builder()
                    .cardNumber("Not Issued")
                    .expiry("N/A")
                    .dailyLimit(0)
                    .status("NOT_ISSUED")
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
    private CreditCardResponse mapCredit(
            Optional<CreditCard> card,
            UUID customerId
    ) {
        if (card.isPresent()) {
            CreditCard c = card.get();
            return CreditCardResponse.builder()
                    .cardNumber(mask(c.getCardNumber()))
                    .creditLimit(c.getCreditLimit())
                    .availableCredit(c.getAvailableCredit())
                    .outstanding(c.getOutstandingAmount())
                    .status("ACTIVE")
                    .build();
        }

        return requestRepo
                .findTopByCustomerIdOrderByRequestedAtDesc(customerId)
                .map(req -> CreditCardResponse.builder()
                        .cardNumber("N/A")
                        .creditLimit(0)
                        .availableCredit(0)
                        .outstanding(0)
                        .status(
                                req.getStatus() == CardStatus.REJECTED
                                        ? "REJECTED: " + req.getRejectionReason()
                                        : "PENDING"
                        )
                        .build()
                )
                .orElse(CreditCardResponse.builder()
                        .cardNumber("Not Issued")
                        .creditLimit(0)
                        .availableCredit(0)
                        .outstanding(0)
                        .status("NOT_ISSUED")
                        .build());
    }


    private List<LoanResponse> mapLoans(List<Loan> loans) {
        return loans.stream()
                .map(loan -> LoanResponse.builder()
                        .loanId(loan.getLoanId())
                        .loanAmount(loan.getAmount() != null ? loan.getAmount().doubleValue() : 0)
                        .emiAmount(loan.getEmiAmount())
                        .interestRate(loan.getInterestRate())
                        .tenureMonths(loan.getTenureMonths())
                        .outstandingAmount(loan.getOutstandingAmount())
                        .loanType(loan.getLoanType() != null ? loan.getLoanType().name() : null)
                        .status(loan.getStatus())
                        .build())
                .toList();
    }

    private List<InsuranceResponse> mapInsurances(List<Insurance> insurances) {
        return insurances.stream()
                .map(ins -> InsuranceResponse.builder()
                        .policyNumber(ins.getInsuranceId())
                        .insuranceType(ins.getInsuranceType() != null ? ins.getInsuranceType().name() : null)
                        .coverageAmount(ins.getCoverageAmount() != null ? ins.getCoverageAmount().doubleValue() : 0)
                        .premiumAmount(ins.getPremiumAmount())
                        .startDate(ins.getStartDate())
                        .endDate(ins.getEndDate())
                        .status(ins.getStatus())
                        .build())
                .toList();
    }

    private List<LinkedAccountResponse> mapLinkedAccounts(
            List<Account> accounts,
            String currentAccountNumber
    ) {
        return accounts.stream()
                .map(acc -> LinkedAccountResponse.builder()
                        .accountNumber(acc.getAccountNumber())
                        .accountType(acc.getAccountType().name())
                        .balance(acc.getBalance().doubleValue())
                        .primary(acc.isPrimaryAccount())
                        .build())
                .toList();
    }

    private NomineeResponse mapNominee(CustomerSummary customer) {
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

    private KycStatusResponse mapKyc(CustomerSummary customer) {
        if (customer != null) {
            return KycStatusResponse.builder()
                    .verified(customer.getKycStatus().equals("APPROVED"))
                    .status(customer.getKycStatus())
                    .build();
        }
        return KycStatusResponse.builder()
                .verified(false)
                .status("PENDING")
                .build();
    }

    private String mask(String value) {
        if (value == null || value.length() < 4) return "XXXX";
        return "XXXX-XXXX-XXXX-" + value.substring(value.length() - 4);
    }
}