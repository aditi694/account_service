package com.bank.account_service.dto.loan;

import com.bank.account_service.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestResponse {

    private String loanId;
    private LoanStatus status;
    private String message;
}
