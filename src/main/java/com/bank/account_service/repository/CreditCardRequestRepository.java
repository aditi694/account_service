package com.bank.account_service.repository;

import com.bank.account_service.entity.CreditCardRequest;
import com.bank.account_service.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditCardRequestRepository extends JpaRepository<CreditCardRequest, UUID> {

    boolean existsByCustomerIdAndStatus(UUID customerId, CardStatus status);

    Optional<CreditCardRequest> findTopByCustomerIdOrderByRequestedAtDesc(UUID customerId);

    List<CreditCardRequest> findByStatus(CardStatus status);
}