package com.bank.account_service.dto.loan;

import com.bank.account_service.enums.LoanType;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class IssueLoanRequest {
    private LoanType loanType;
    private BigDecimal amount;
}
