package com.example.loansystem.exceptions;

public class LoanAlreadyPaidException extends RuntimeException{
    public LoanAlreadyPaidException(String message) {
        super(message);
    }
}
