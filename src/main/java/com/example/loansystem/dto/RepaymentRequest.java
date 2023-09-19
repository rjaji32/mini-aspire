package com.example.loansystem.dto;

public class RepaymentRequest {
    private Long loanId;
    private double repaymentAmount;
    public RepaymentRequest() {
    }

    public RepaymentRequest(Long loanId, double repaymentAmount) {
        this.loanId = loanId;
        this.repaymentAmount = repaymentAmount;
    }

    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public double getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(double repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }
}
