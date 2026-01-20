package com.bank.account_service.dto.loan;

import com.bank.account_service.enums.LoanStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanApprovalResponse {

    private String loanId;
    private LoanStatus previousStatus;
    private LoanStatus currentStatus;
    private String message;
}
