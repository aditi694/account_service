package com.bank.account_service.repository;

import com.bank.account_service.dto.card.CreditCardRequest;
import com.bank.account_service.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditCardRequestRepository
        extends JpaRepository<CreditCardRequest, UUID> {

    boolean existsByCustomerIdAndStatus(UUID customerId, CardStatus status);
    Optional<CreditCardRequest>
    findTopByCustomerIdOrderByRequestedAtDesc(UUID customerId);

}
