package com.bank.account_service.dto.loan.response;

import com.bank.account_service.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApprovalResponse {

    private String loanId;
    private LoanStatus previousStatus;
    private LoanStatus currentStatus;
    private String message;
}
