package com.bank.account_service.dto.account;

import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.dto.loan.LoanResponse;
import com.bank.account_service.dto.response.*;
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

    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private BankBranchDetails bankBranch;
    private String status;

    private DebitCardResponse debitCard;
    private CreditCardResponse creditCard;

    private List<LoanResponse> loans;
    private List<InsuranceResponse> insurances;

    private LimitsResponse limits;
    private NomineeResponse nominee;
    private KycStatusResponse kyc;

    private List<LinkedAccountResponse> linkedAccounts;
    @Data
    @Builder
    public static class BankBranchDetails {
        private String ifscCode;
        private String bankName;
        private String branchName;
        private String city;
        private String address;
    }
}