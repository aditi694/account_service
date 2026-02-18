package com.bank.account_service.dto.account.response;

import com.bank.account_service.dto.card.response.CreditCardResponse;
import com.bank.account_service.dto.card.response.DebitCardResponse;
import com.bank.account_service.dto.insurance.response.InsuranceResponse;
import com.bank.account_service.dto.loan.response.LoanResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDashboardResponse {

    private UUID accountId;
    private UUID customerId;
    private String customerName;

    private String accountNumber;
    private String accountType;
    private String accountTypeDescription;

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