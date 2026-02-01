package com.bank.account_service.dto.account;

import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AccountDashboardResponse {

    private UUID accountId;
    private UUID customerId;
    private String customerName;

    private String accountNumber;
    private String accountType;
    private String accountTypeDescription;  // ✅ Add this

    private BigDecimal balance;
    private BankBranchDetails bankBranch;
    private String status;
    private String statusMessage;

    private DebitCardResponse debitCard;
    private CreditCardResponse creditCard;

    private List<LoanResponse> loans;
    private List<InsuranceResponse> insurances;

    private Limits limits;
    private Nominee nominee;
    private KycStatus kyc;

    private List<LinkedAccount> linkedAccounts;

    // ================= INNER DTOs =================

    @Data
    @Builder
    public static class BankBranchDetails {
        private String ifscCode;
        private String bankName;
        private String branchName;
        private String city;
        private String address;
    }

    @Data
    @Builder
    public static class Limits {
        private double dailyTransactionLimit;
        private double perTransactionLimit;
    }

    @Data
    @Builder
    public static class Nominee {
        private String name;
        private String relation;
    }

    @Data
    @Builder
    public static class KycStatus {
        private boolean verified;
        private String status;
    }

    @Data
    @Builder
    public static class LinkedAccount {
        private String accountNumber;
        private String accountType;
        private double balance;
        private boolean primary;
    }
}