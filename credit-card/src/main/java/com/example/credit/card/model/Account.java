package com.example.credit.card.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Document
public class Account {

    @Id
    private String accountId;

    private BigDecimal availableCreditLimit;

    private BigDecimal availableWithdrawalLimit;

    public Account(){

    }

    public Account(BigDecimal availableCreditLimit, BigDecimal availableWithdrawalLimit) {
        this.setAvailableCreditLimit(availableCreditLimit);
        this.setAvailableWithdrawalLimit(availableWithdrawalLimit);
    }

    public Account(Double availableCreditLimit, Double availableWithdrawalLimit) {
        this(new BigDecimal(availableCreditLimit), new BigDecimal(availableWithdrawalLimit));
    }

    public Account(String accountId, BigDecimal availableCreditLimit, BigDecimal availableWithdrawalLimit) {
        this(availableCreditLimit, availableWithdrawalLimit);
        this.setAccountId(accountId);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAvailableCreditLimit() {
        return availableCreditLimit;
    }

    public void setAvailableCreditLimit(BigDecimal availableCreditLimit) {
        this.availableCreditLimit = ofNullable(availableCreditLimit).orElse(BigDecimal.ZERO)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal getAvailableWithdrawalLimit() {
        return availableWithdrawalLimit;
    }

    public void setAvailableWithdrawalLimit(BigDecimal availableWithdrawalLimit) {
        this.availableWithdrawalLimit = ofNullable(availableWithdrawalLimit).orElse(BigDecimal.ZERO)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", availableCreditLimit=" + availableCreditLimit +
                ", availableWithdrawalLimit=" + availableWithdrawalLimit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(accountId);
    }
}
