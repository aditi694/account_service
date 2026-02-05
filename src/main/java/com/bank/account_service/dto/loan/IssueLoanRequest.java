package com.bank.account_service.dto.loan;

import com.bank.account_service.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IssueLoanRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}