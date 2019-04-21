package com.example.credit.card.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

public class PaymentRequest {

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("amount")
    private BigDecimal amount;

    public PaymentRequest() {

    }

    public PaymentRequest(String accountId, BigDecimal amount) {
        this.setAccountId(accountId);
        this.setAmount(amount);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = ofNullable(amount).orElse(BigDecimal.ZERO)
            .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    @Override
    public String toString() {
        return "PaymentRequest{" + "accountId='" + accountId + '\'' + ", amount=" + amount + '}';
    }
}
