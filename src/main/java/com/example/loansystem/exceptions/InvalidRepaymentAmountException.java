package com.example.loansystem.exceptions;

public class InvalidRepaymentAmountException extends RuntimeException{
    public InvalidRepaymentAmountException(String message) {
        super(message);
    }
}
