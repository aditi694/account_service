package com.bank.account_service.entity;

import com.bank.account_service.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "credit_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private double creditLimit;

    @Column(nullable = false)
    private double availableCredit;

    @Column(nullable = false)
    private double outstandingAmount = 0.0;

    @Enumerated(EnumType.STRING)
    private CardStatus status;
}
