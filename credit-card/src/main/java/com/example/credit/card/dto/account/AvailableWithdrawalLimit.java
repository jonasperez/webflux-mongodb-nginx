package com.example.credit.card.dto.account;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AvailableWithdrawalLimit {

    private BigDecimal amount;

    public AvailableWithdrawalLimit(){
    }

    public AvailableWithdrawalLimit(BigDecimal amount){
        this.setAmount(amount);
    }

    public AvailableWithdrawalLimit(Double amount){
        this.setAmount(new BigDecimal(amount));
    }

    public Optional<BigDecimal> getAmount() {
        return ofNullable(amount);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
