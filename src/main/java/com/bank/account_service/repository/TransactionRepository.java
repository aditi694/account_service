//package com.bank.account_service.repository;
//
//import com.bank.account_service.entity.Transaction;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.UUID;
//
//public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
//
//    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(
//            UUID accountId,
//            Pageable pageable
//    );
//}
