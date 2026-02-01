// account-service/.../kafka/TransactionCommandConsumer.java
package com.bank.account_service.kafka.consumer;

import com.bank.account_service.entity.Account;
import com.bank.account_service.kafka.event.*;
import com.bank.account_service.kafka.producer.AccountBalanceEventProducer;
import com.bank.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Component
@RequiredArgsConstructor
public class TransactionCommandConsumer {

    private final AccountRepository repo;
    private final AccountBalanceEventProducer producer;

    @KafkaListener(topics = "transaction-commands", groupId = "account-service")
    public void handle(TransactionCommandEvent event) {

        Account acc = repo.findByAccountNumber(event.account())
                .orElseThrow();

        try {
            switch (event.step()) {
                case "DEBIT" -> acc.debit(event.amount(), event.transactionId());
                case "CREDIT", "COMPENSATE" -> acc.credit(event.amount(), event.transactionId());
            }

            repo.save(acc);

            producer.publish(success(event, acc.getBalance()));
        } catch (Exception ex) {
            producer.publish(failure(event));
        }
    }

    private AccountBalanceEvent success(TransactionCommandEvent e, BigDecimal bal) {
        return new AccountBalanceEvent(
                1, UUID.randomUUID().toString(), e.transactionId(),
                e.step(), "SUCCESS", bal, LocalDateTime.now()
        );
    }

    private AccountBalanceEvent failure(TransactionCommandEvent e) {
        return new AccountBalanceEvent(
                1, UUID.randomUUID().toString(), e.transactionId(),
                e.step(), "FAILED", null, LocalDateTime.now()
        );
    }
}