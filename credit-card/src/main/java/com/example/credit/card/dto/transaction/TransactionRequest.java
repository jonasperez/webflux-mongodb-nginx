package com.example.credit.card.dto.transaction;

import com.example.credit.card.model.OperationType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

public class TransactionRequest {

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty(value = "operation_type_id")
    private int operationTypeId;

    private BigDecimal amount;

    public TransactionRequest(){

    }

    public TransactionRequest(String accountId, OperationType operationTypeId, BigDecimal amount){
        this.setAccountId(accountId);
        this.setOperationTypeId(operationTypeId.getOperationTypeId());
        this.setAmount(amount);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    public int getOperationTypeId() {
        return operationTypeId;
    }

    public void setOperationTypeId(int operationTypeId) {
        this.operationTypeId = operationTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = ofNullable(amount).orElse(BigDecimal.ZERO)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
}
