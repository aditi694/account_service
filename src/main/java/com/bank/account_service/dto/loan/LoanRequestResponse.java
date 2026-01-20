package com.bank.account_service.dto.loan;

import com.bank.account_service.enums.LoanStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanRequestResponse {

    private String loanId;
    private LoanStatus status;
    private String message;
}
