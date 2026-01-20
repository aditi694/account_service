package com.bank.account_service.entity;

import com.bank.account_service.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name = "debit_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitCard {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    private LocalDate expiryDate;

    private int dailyLimit;

    private int usedToday;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private LocalDate issuedDate;

}
