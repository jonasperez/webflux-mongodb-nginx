package com.example.credit.card.repository;

import com.example.credit.card.model.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

    @Query("{'accountId': { $eq: ?0 }, 'operationType': { $ne: 'PAYMENT' }, 'balance': { $ne: '0.00' } }")
    Flux<Transaction> findTransactionsUnpaidByAccount(String accountId, Sort sort);

    @Query("{'accountId': { $eq: ?0 }, 'operationType': { $ne: 'PAYMENT' } }")
    Flux<Transaction> findTransactionsByAccount(String accountId, Sort sort);

    @Query("{'accountId': { $eq: ?0 }, 'operationType': { $eq: 'PAYMENT' } }")
    Flux<Transaction> findPaymentsByAccount(String accountId);

}
