package com.example.credit.card.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Document
public class Transaction {

    @Id
    private String transactionId;

    @Transient
    private Account account;

    private String accountId;
    private OperationType operationType;
    private BigDecimal amount;
    private BigDecimal balance;
    private Date eventDate;
    private Date dueDate;

    private Operation operation;

    public Transaction(){

    }

    public Transaction(Account account, OperationType operationType, BigDecimal amount, BigDecimal balance, Date eventDate, Date dueDate) {
        this.setAccount(account);
        this.setOperationType(operationType);
        this.setAmount(amount);
        this.setBalance(balance);
        this.setEventDate(eventDate);
        this.setDueDate(dueDate);
    }

    public Transaction(Account account, OperationType operationType, BigDecimal amount, BigDecimal balance) {
        this(account, operationType, amount, balance, new Date(), new Date());
    }

    public Transaction(String transactionId, Account account, OperationType operationType, BigDecimal amount, BigDecimal balance) {
        this(account, operationType, amount, balance, new Date(), new Date());
        this.setTransactionId(transactionId);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setAccount(Account account) {
        this.accountId = ofNullable(account).orElse(new Account()).getAccountId();
        this.account = account;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operation = new Operation(operationType);
        this.operationType = operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = ofNullable(amount).orElse(BigDecimal.ZERO)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public void setBalance(BigDecimal balance) {
        this.balance = ofNullable(balance).orElse(BigDecimal.ZERO)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", operationType=" + operationType +
                ", amount=" + amount +
                ", balance=" + balance +
                ", eventDate=" + eventDate +
                ", dueDate=" + dueDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(transactionId);
    }
}
