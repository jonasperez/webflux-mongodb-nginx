package com.example.credit.card.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Optional.ofNullable;

public class AccountRequest {

    @JsonProperty("available_credit_limit")
    private AvailableCreditLimit availableCreditLimit;

    @JsonProperty("available_withdrawal_limit")
    private AvailableWithdrawalLimit availableWithdrawalLimit;

    public AccountRequest(){

    }

    public AccountRequest(AvailableCreditLimit availableCreditLimit, AvailableWithdrawalLimit availableWithdrawalLimit){
        this.setAvailableCreditLimit(availableCreditLimit);
        this.setAvailableWithdrawalLimit(availableWithdrawalLimit);
    }

    public AvailableCreditLimit getAvailableCreditLimit() {
        return ofNullable(availableCreditLimit).orElseGet(AvailableCreditLimit::new);
    }

    public void setAvailableCreditLimit(AvailableCreditLimit availableCreditLimit) {
        this.availableCreditLimit = availableCreditLimit;
    }

    public AvailableWithdrawalLimit getAvailableWithdrawalLimit() {
        return ofNullable(availableWithdrawalLimit).orElseGet(AvailableWithdrawalLimit::new);
    }

    public void setAvailableWithdrawalLimit(AvailableWithdrawalLimit availableWithdrawalLimit) {
        this.availableWithdrawalLimit = availableWithdrawalLimit;
    }
}
