package com.example.loansystem.exceptions;

public class InvalidUserAuthority extends RuntimeException{
    public InvalidUserAuthority(String message) {
        super(message);
    }
}
