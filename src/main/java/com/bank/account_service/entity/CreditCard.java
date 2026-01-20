package com.bank.account_service.entity;

import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.enums.CardType;
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

    private UUID customerId;

    @Column(unique = true)
    private String cardNumber;

    private double creditLimit;
    private double availableCredit;
    private double outstandingAmount;

    @Enumerated(EnumType.STRING)
    private CardStatus status;
}
