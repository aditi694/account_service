//// src/main/java/com/bank/account_service/kafka/consumer/TransactionCommandConsumer.java
//package com.bank.account_service.kafka.consumer;
//
//import com.bank.account_service.entity.Account;
//import com.bank.account_service.kafka.producer.AccountBalanceEventProducer;
//import com.bank.account_service.repository.AccountRepository;
//import com.bank.account_service.kafka.event.AccountBalanceEvent;
//import com.bank.account_service.kafka.event.TransactionCommandEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//import java.math.BigDecimal;
//
//@Component
//@RequiredArgsConstructor
//public class TransactionCommandConsumer {
//
//    private final AccountRepository repo;
//    private final AccountBalanceEventProducer producer;
//
//    @KafkaListener(topics = "transaction-command", groupId = "account-service")
//    public void handle(TransactionCommandEvent event) {
//
//        try {
//            Account acc = repo.findByAccountNumber(event.accountNumber())
//                    .orElseThrow();
//
//            switch (event.step()) {
//                case "DEBIT" ->
//                        acc.debit(event.amount(), event.transactionId());
//                case "CREDIT", "COMPENSATE" ->
//                        acc.credit(event.amount(), event.transactionId());
//            }
//
//            repo.save(acc);
//
//            producer.publish(new AccountBalanceEvent(
//                    event.transactionId(),
//                    event.step(),
//                    "SUCCESS",
//                    event.accountNumber(),
//                    acc.getBalance(),
//                    null
//            ));
//
//        } catch (Exception ex) {
//            producer.publish(new AccountBalanceEvent(
//                    event.transactionId(),
//                    event.step(),
//                    "FAILED",
//                    event.accountNumber(),
//                    null,
//                    ex.getMessage()
//            ));
//        }
//    }
//}