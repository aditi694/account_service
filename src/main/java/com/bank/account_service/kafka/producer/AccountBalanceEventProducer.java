// account-service/.../kafka/AccountBalanceEventProducer.java
package com.bank.account_service.kafka.producer;

import com.bank.account_service.kafka.event.AccountBalanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountBalanceEventProducer {

    private final KafkaTemplate<String, AccountBalanceEvent> kafkaTemplate;

    public void publish(
            String transactionId,
            String step,
            String status,
            String accountNumber,
            BigDecimal amount,
            BigDecimal balanceAfter
    ) {
        kafkaTemplate.send(
                "account-balance-events",
                transactionId,
                new AccountBalanceEvent(
                        1,
                        UUID.randomUUID().toString(),
                        transactionId,
                        step,
                        status,
                        accountNumber,
                        amount,
                        balanceAfter,
                        LocalDateTime.now()
                )
        );
    }
}