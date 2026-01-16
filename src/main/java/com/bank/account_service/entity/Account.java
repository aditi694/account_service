package com.bank.account_service.entity;

import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import com.bank.account_service.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID customerId;

    private String customerName;

    @Column(unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private String bankName;
    private String branchName;
    private String ifscCode;

    private BigDecimal balance;

    private String currency;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Version
    private Long version;

    private LocalDateTime openedAt;
    private LocalDateTime updatedAt;

    public void debit(BigDecimal amount) {
        validateActive();
        validateAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw BusinessException.insufficientBalance();
        }
        balance = balance.subtract(amount);
    }

    private void validateActive() {
        if (status != AccountStatus.ACTIVE) {
            throw BusinessException.accountNotActive();
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.invalidAmount();
        }
    }
}
