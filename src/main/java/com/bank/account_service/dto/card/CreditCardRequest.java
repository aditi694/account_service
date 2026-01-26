package com.bank.account_service.dto.card;

import com.bank.account_service.enums.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_card_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardRequest {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID customerId;
    private String cardHolderName;

    @Enumerated(EnumType.STRING)
    private CardStatus status; // PENDING / APPROVED / REJECTED

    private Double approvedLimit;
    private String rejectionReason;

    private LocalDateTime requestedAt;
    private LocalDateTime decidedAt;
}