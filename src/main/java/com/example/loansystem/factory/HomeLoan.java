package com.example.loansystem.factory;

import com.example.loansystem.factory.loaninterfaces.LoanTypeInfo;

public class HomeLoan implements LoanTypeInfo {
    @Override
    public double getInterestRate() {
        return 0.08;
    }
    @Override
    public double getInterest(double principal, double rateOfInterest, int duration) {
        return principal*rateOfInterest*duration/(100*12);
    }
}
