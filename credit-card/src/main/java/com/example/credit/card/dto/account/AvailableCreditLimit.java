package com.example.credit.card.dto.account;


import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AvailableCreditLimit {

    private BigDecimal amount;

    public AvailableCreditLimit(){

    }

    public AvailableCreditLimit(BigDecimal amount){
        this.setAmount(amount);
    }

    public AvailableCreditLimit(Double amount){
        this.setAmount(new BigDecimal(amount));
    }

    public Optional<BigDecimal> getAmount() {
        return ofNullable(amount);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
