package com.bank.account_service.repository;

import com.bank.account_service.entity.DebitCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DebitCardRepository extends JpaRepository<DebitCard, UUID> {

    Optional<DebitCard> findByAccountNumber(String accountNumber);
}