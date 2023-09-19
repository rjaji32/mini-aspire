package com.example.loansystem.dto;

import com.example.loansystem.model.LoanType;

import java.time.LocalDate;

public class LoanRequest {
    private double amountRequired;
    private int loanTerm;
    private LocalDate requestDate;

    private Long userId;

    private LoanType loanType;

    public LoanRequest() {
    }

    public LoanRequest(double amountRequired, int loanTerm, LocalDate requestDate, Long userId, LoanType loanType) {
        this.amountRequired = amountRequired;
        this.loanTerm = loanTerm;
        this.requestDate = requestDate;
        this.userId = userId;
        this.loanType = loanType;
    }

    public double getAmountRequired() {
        return amountRequired;
    }

    public void setAmountRequired(double amountRequired) {
        this.amountRequired = amountRequired;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }
}

