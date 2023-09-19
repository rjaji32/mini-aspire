package com.example.loansystem.strategy;

import com.example.loansystem.model.EMI;
import com.example.loansystem.model.EMIStatus;
import com.example.loansystem.model.Loan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FixedEMIStrategy implements RepaymentStrategy {
    @Override
    public List<EMI> generateEMIs(Loan loan) {
        List<EMI> emis = new ArrayList<>();
        double amountToBePaid = loan.getAmountToBePaid();
        int loanTermInWeeks = loan.getLoanTerm();
        double emiAmount = amountToBePaid / loanTermInWeeks;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loan.getStartDate());

        for (int week = 0; week < loanTermInWeeks; week++) {
            Date dueDate = calendar.getTime();
            emis.add(new EMI(loan.getId(), loan.getUserId(),emiAmount, dueDate, EMIStatus.PENDING));

            // Increment the calendar by 7 days (1 week)
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        return emis;
    }
}
