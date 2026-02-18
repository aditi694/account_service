package com.bank.account_service.dto.loan.response;

import com.bank.account_service.enums.LoanStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanStatusResponse {
    private String loanId;
    private String loanType;
    private LoanStatus status;
    private String statusMessage;
}