package com.bank.account_service.entity;

import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "primary_account")
    private boolean primaryAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "requires_password_change")
    private boolean requiresPasswordChange;

    @Column(name = "opening_date")
    private LocalDateTime openingDate;

    @Column(name = "ifsc_code")
    private String ifscCode;
    // 🆕 idempotency guard
    @Column(name = "last_processed_txn", unique = true)
    private String lastProcessedTransactionId;

}