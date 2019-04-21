package com.example.credit.card.service;

import com.example.credit.card.dto.account.AccountRequest;
import com.example.credit.card.exception.AvailableCreditLimitException;
import com.example.credit.card.model.Account;
import com.example.credit.card.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Mono<Account> save(Account account, AccountRequest accountRequest){
        BigDecimal availableCreditLimit = accountRequest
                .getAvailableCreditLimit()
                .getAmount().map(amount -> amount.add(account.getAvailableCreditLimit()))
                .orElseGet(() -> account.getAvailableCreditLimit());

        BigDecimal availableWithdrawalLimit = accountRequest
                .getAvailableWithdrawalLimit()
                .getAmount().map(amount -> amount.add(account.getAvailableWithdrawalLimit()))
                .orElseGet(() -> account.getAvailableWithdrawalLimit());

        return this.save(new Account(account.getAccountId(), availableCreditLimit, availableWithdrawalLimit));
    }

    public Mono<Account> insert(AccountRequest accountRequest){
        return Mono.create(callback -> {
            BigDecimal availableCreditLimit = accountRequest
                    .getAvailableCreditLimit()
                    .getAmount()
                    .orElseThrow(() -> {
                        AvailableCreditLimitException throwable = new AvailableCreditLimitException("Available Credit Limit Is Required");
                        callback.error(throwable);
                        return throwable;
                    });

            BigDecimal availableWithdrawalLimit = accountRequest
                    .getAvailableWithdrawalLimit()
                    .getAmount()
                    .orElseThrow(() -> {
                        AvailableCreditLimitException throwable = new AvailableCreditLimitException("Available Withdrawal Limit Is Required");
                        callback.error(throwable);
                        return throwable;
                    });

            this.insert(new Account(availableCreditLimit, availableWithdrawalLimit)).subscribe(account -> callback.success(account));

        });
    }

    public Mono<Account> findById(String accountId){
        return accountRepository.findById(accountId);
    }

    public Flux<Account> findAll(){
        return accountRepository.findAll();
    }

    public Mono<Account> save(Account account){
        return accountRepository.save(account);
    }

    public Mono<Account> insert(Account account){
        return accountRepository.insert(account);
    }

}
