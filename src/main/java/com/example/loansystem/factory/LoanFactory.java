package com.example.loansystem.factory;

import com.example.loansystem.factory.loaninterfaces.LoanTypeInfo;
import com.example.loansystem.model.LoanType;

public class LoanFactory {
    public static LoanTypeInfo getInterestRate(LoanType loanType) {
        switch (loanType) {
            case PERSONAL:
                return new PersonalLoan();
            case HOME:
                return new HomeLoan();
            case CAR:
                return new CarLoan();
            default:
                throw new IllegalArgumentException("Invalid loan type: " + loanType);
        }
    }
}
