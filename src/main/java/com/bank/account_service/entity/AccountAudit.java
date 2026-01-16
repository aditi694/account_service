package com.bank.account_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_audit")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AccountAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID accountId;

    private String action;

    private BigDecimal oldBalance;
    private BigDecimal newBalance;

    private String performedBy;
    private String performedByRole;

    private LocalDateTime timestamp;
}
