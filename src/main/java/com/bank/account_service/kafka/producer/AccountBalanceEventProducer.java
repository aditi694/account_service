// account-service/.../kafka/AccountBalanceEventProducer.java
package com.bank.account_service.kafka.producer;

import com.bank.account_service.kafka.event.AccountBalanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountBalanceEventProducer {

    private final KafkaTemplate<String, AccountBalanceEvent> kafkaTemplate;

    public void publish(AccountBalanceEvent event) {
        kafkaTemplate.send("account-balance-events", event.transactionId(), event);
    }
}