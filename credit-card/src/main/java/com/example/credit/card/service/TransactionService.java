package com.example.credit.card.service;

import com.example.credit.card.dto.payment.PaymentRequest;
import com.example.credit.card.dto.transaction.TransactionRequest;
import com.example.credit.card.exception.AvailableCreditLimitException;
import com.example.credit.card.exception.AvailableWithdrawalLimitException;
import com.example.credit.card.model.Account;
import com.example.credit.card.model.OperationType;
import com.example.credit.card.model.Transaction;
import com.example.credit.card.repository.AccountRepository;
import com.example.credit.card.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Date;

import static com.example.credit.card.model.OperationType.PAYMENT;
import static com.example.credit.card.model.OperationType.WITHDRAWAL;

@Service
public class TransactionService {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    public Mono<Transaction> insert(TransactionRequest transactionRequest) {
        return accountService.findById(transactionRequest.getAccountId()).flatMap(account ->
            Mono.create(callback -> {
                Transaction transaction = new Transaction();
                transaction.setAccount(account);
                transaction.setOperationType(OperationType.getById(transactionRequest.getOperationTypeId()));
                transaction.setAmount(transactionRequest.getAmount());
                transaction.setBalance(transactionRequest.getAmount());
                transaction.setEventDate(new Date());
                transaction.setDueDate(new Date());

                if(transaction.getOperationType() == WITHDRAWAL) {
                    BigDecimal balance = account.getAvailableWithdrawalLimit().add(transaction.getAmount());
                    if (balance.compareTo(ZERO) >= 0) account.setAvailableWithdrawalLimit(balance);
                    else {
                        callback.error(new AvailableWithdrawalLimitException("Available Withdrawal Limit Exceeded"));
                        return;
                    }
                }

                if(transaction.getOperationType() != WITHDRAWAL){
                    BigDecimal balance = account.getAvailableCreditLimit().add(transaction.getAmount());
                    if (balance.compareTo(ZERO) >= 0) account.setAvailableCreditLimit(balance);
                    else {
                        callback.error(new AvailableCreditLimitException("Available Credit Limit Exceeded"));
                        return;
                    }
                }
                accountService.save(account).subscribe();
                Mono<Transaction> transactionMono = this.insert(transaction);
                transactionMono.subscribe(transaction1 -> callback.success(transaction1));
            })
        );
    }

    public Mono<String> doPayment(final PaymentRequest request) {
        return Mono.create(callback ->
                this.accountService.findById(request.getAccountId()).subscribe(account ->
                this.findTransactionsUnpaidByAccountOrdered(account)
                    .reduceWith(() -> request, (payment, transaction) -> this.doPayment(account, payment, transaction))
                    .doOnSuccess(success -> callback.success("SUCCESS"))
                    .doOnError(error -> callback.error(error))
                    .subscribe(paymentRequest -> {
                        Transaction paymentTransaction = new Transaction(account, PAYMENT, request.getAmount(), paymentRequest.getAmount());
                        this.save(paymentTransaction).subscribe();
                })
            )
        );
    }

    private PaymentRequest doPayment(Account account, PaymentRequest payment, Transaction transaction){
        BigDecimal balance = payment.getAmount().add(transaction.getBalance());

        if (transaction.getBalance().compareTo(ZERO) == 0) return payment;
        if (balance.compareTo(ZERO) > 0) transaction.setBalance(ZERO);
        else if (balance.compareTo(ZERO) <= 0 && transaction.getBalance().compareTo(balance) <= 0) transaction.setBalance(balance);

        discountLimit(account, transaction);

        this.save(transaction).subscribe();

        if (balance.compareTo(ZERO) <= 0) return new PaymentRequest(payment.getAccountId(), ZERO);
        else return new PaymentRequest(payment.getAccountId(), balance);
    }

    private void discountLimit(Account account, Transaction transaction){
        BigDecimal limit = transaction.getAmount().subtract(transaction.getBalance());
        if(transaction.getOperationType() == WITHDRAWAL) {
            account.setAvailableWithdrawalLimit(account.getAvailableWithdrawalLimit().subtract(limit));
        }
        if(transaction.getOperationType() != WITHDRAWAL){
            account.setAvailableCreditLimit(account.getAvailableCreditLimit().subtract(limit));
        }
        this.accountService.save(account).subscribe();
    }

    public Mono<Transaction> insert(Transaction transaction){
        return transactionRepository.insert(transaction);
    }

    public Mono<Transaction> save(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public Flux<Transaction> findPaymentsByAccount(String accountId){
        return this.accountService.findById(accountId).flatMapMany(account ->
            this.findPaymentsByAccount(account)
        );
    }

    public Flux<Transaction> findPaymentsByAccount(Account account){
        return transactionRepository.findPaymentsByAccount(account.getAccountId());
    }

    public Flux<Transaction> findAllOrdered(){
        return transactionRepository.findAll(new Sort(Sort.Direction.ASC, "operation.chargeOrder"));
    }

    public Flux<Transaction> findTransactionsUnpaidByAccountOrdered(Account account){
        return transactionRepository.findTransactionsUnpaidByAccount(account.getAccountId(),
            new Sort(Sort.Direction.ASC, "operation.chargeOrder"));
    }

    public Flux<Transaction> findTransactionsdByAccountOrdered(String accountId){
        return this.accountService.findById(accountId).flatMapMany(account ->
            this.findTransactionsdByAccountOrdered(account)
        );
    }

    public Flux<Transaction> findTransactionsdByAccountOrdered(Account account){
        return transactionRepository.findTransactionsByAccount(account.getAccountId(),
            new Sort(Sort.Direction.ASC, "operation.chargeOrder"));
    }

}
