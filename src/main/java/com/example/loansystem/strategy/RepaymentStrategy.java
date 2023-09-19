package com.example.loansystem.strategy;

import com.example.loansystem.model.EMI;
import com.example.loansystem.model.Loan;

import java.util.List;

public interface RepaymentStrategy {
    List<EMI> generateEMIs(Loan loan);
}
