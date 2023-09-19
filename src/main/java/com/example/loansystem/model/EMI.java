package com.example.loansystem.model;

import java.util.Date;

public class EMI {
    private long loanId;
    private long userId;
    private double amount;
    private Date dueDate;

    private EMIStatus emiStatus;

    public EMI(){

    }

    public EMI(long loanId, long userId, double amount, Date dueDate, EMIStatus emiStatus) {
        this.loanId = loanId;
        this.userId = userId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.emiStatus = emiStatus;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public EMIStatus getEmiStatus() {
        return emiStatus;
    }

    public void setEmiStatus(EMIStatus emiStatus) {
        this.emiStatus = emiStatus;
    }
}
