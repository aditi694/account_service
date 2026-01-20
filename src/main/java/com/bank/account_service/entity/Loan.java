package com.bank.account_service.entity;

import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    private String loanId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private BigDecimal amount;

    private LoanType loanType;

    private Double interestRate;
    private Integer tenureMonths;
    private Double emiAmount;
    private Double outstandingAmount;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;
}
