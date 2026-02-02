// src/main/java/com/bank/account_service/kafka/consumer/TransactionCommandConsumer.java
package com.bank.account_service.kafka.consumer;

import com.bank.account_service.entity.Account;
import com.bank.account_service.kafka.producer.AccountBalanceEventProducer;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.kafka.event.AccountBalanceEvent;
import com.bank.account_service.kafka.event.TransactionCommandEvent;
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

        Account acc = repo.findByAccountNumber(event.accountNumber())
                .orElseThrow();

        try {
            if ("DEBIT".equals(event.step())) {
                acc.debit(event.amount(), event.transactionId());
            } else if ("CREDIT".equals(event.step())) {
                acc.credit(event.amount(), event.transactionId());
            } else if ("COMPENSATE_DEBIT".equals(event.step())) {
                acc.credit(event.amount(), event.transactionId());
            }

            repo.save(acc);

            producer.publish(
                    event.transactionId(),
                    event.step(),
                    "SUCCESS",
                    event.accountNumber(),
                    event.amount(),
                    acc.getBalance()
            );

        } catch (Exception ex) {
            producer.publish(
                    event.transactionId(),
                    event.step(),
                    "FAILED",
                    event.accountNumber(),
                    event.amount(),
                    null
            );
        }
    }
}