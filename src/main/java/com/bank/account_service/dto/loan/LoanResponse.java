package com.bank.account_service.dto.loan;

import com.bank.account_service.enums.LoanStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanResponse {

    private String loanId;
    private String loanType;
    private Double loanAmount;
    private Double interestRate;
    private Integer tenureMonths;
    private Double emiAmount;
    private Double outstandingAmount;
    private LoanStatus status;
}
