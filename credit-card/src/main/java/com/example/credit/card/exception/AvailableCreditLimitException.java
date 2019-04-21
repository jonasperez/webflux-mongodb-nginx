package com.example.credit.card.exception;

public class AvailableCreditLimitException extends RuntimeException {

    public AvailableCreditLimitException(String message){
        super(message);
    }

}
