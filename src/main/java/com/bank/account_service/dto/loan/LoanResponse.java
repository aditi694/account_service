package com.bank.account_service.dto.loan;

import com.bank.account_service.enums.LoanStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String statusMessage;
}