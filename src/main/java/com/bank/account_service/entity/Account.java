package com.bank.account_service.entity;

import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID customerId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    private String currency;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Version
    private Long version;

    public void credit(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.invalidAmount();
        }

        if (this.status != AccountStatus.ACTIVE) {
            throw BusinessException.accountFrozen();
        }

        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.invalidAmount();
        }

        if (this.status != AccountStatus.ACTIVE) {
            throw BusinessException.accountFrozen();
        }

        if (this.balance.compareTo(amount) < 0) {
            throw BusinessException.insufficientBalance();
        }

        this.balance = this.balance.subtract(amount);
    }

}
