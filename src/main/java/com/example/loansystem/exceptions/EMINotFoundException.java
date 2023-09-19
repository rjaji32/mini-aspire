package com.example.loansystem.exceptions;

public class EMINotFoundException extends RuntimeException{
    public EMINotFoundException(String message) {
        super(message);
    }
}
