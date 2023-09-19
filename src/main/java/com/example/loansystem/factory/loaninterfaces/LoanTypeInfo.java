package com.example.loansystem.factory.loaninterfaces;

public interface LoanTypeInfo {
    double getInterestRate();
    double getInterest(double principal, double rateOfInterest, int duration);
}
