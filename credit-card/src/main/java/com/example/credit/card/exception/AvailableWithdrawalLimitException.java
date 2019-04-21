package com.example.credit.card.exception;

public class AvailableWithdrawalLimitException extends RuntimeException {

    public AvailableWithdrawalLimitException(String message){
        super(message);
    }
}
