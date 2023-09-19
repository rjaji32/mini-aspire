package com.example.loansystem.model;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private double amountRequired;
    private int loanTerm;

    private double amountToBePaid;

    private double interestRate;
    private LocalDate requestDate;
    private Date startDate;
    private LoanStatus status;

    private LoanType loanType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Loan() {
    }

    public Loan(double amountRequired, int loanTerm, LocalDate requestDate, LoanStatus status, double amountToBePaid, double interestRate, Date startDate, LoanType loanType) {
        this.amountRequired = amountRequired;
        this.loanTerm = loanTerm;
        this.requestDate = requestDate;
        this.status = status;
        this.amountToBePaid = amountToBePaid;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.loanType = loanType;
    }

    public static LoanBuilder builder() {
        return new LoanBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getAmountRequired() {
        return amountRequired;
    }

    public void setAmountRequired(double amountRequired) {
        this.amountRequired = amountRequired;
    }

    public double getAmountToBePaid() {
        return amountToBePaid;
    }

    public void setAmountToBePaid(double amountToBePaid) {
        this.amountToBePaid = amountToBePaid;
    }


    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
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

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getStartDate() {return startDate;}
    public void setStartDate(Date startDate) {this.startDate = startDate;}

    public LoanType getLoanType() {
        return loanType;
    }
    public void setLoanType(LoanType loanType){
        this.loanType = loanType;
    }

    public static class LoanBuilder {
        private double amountRequired;
        private int loanTerm;
        private long userId;
        private LoanStatus status;
        private double amountToBePaid;
        private double interestRate;
        private LoanType loanType;
        // Other fields...

        public LoanBuilder amountRequired(double amountRequired) {
            this.amountRequired = amountRequired;
            return this;
        }

        public LoanBuilder loanTerm(int loanTerm) {
            this.loanTerm = loanTerm;
            return this;
        }

        public LoanBuilder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public LoanBuilder status(LoanStatus status) {
            this.status = status;
            return this;
        }

        public LoanBuilder amountToBePaid(double amountToBePaid) {
            this.amountToBePaid = amountToBePaid;
            return this;
        }

        public LoanBuilder interestRate(double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public LoanBuilder loanType(LoanType loanType) {
            this.loanType = loanType;
            return this;
        }
        public Loan build() {
            Loan loan = new Loan();
            loan.amountRequired = this.amountRequired;
            loan.loanTerm = this.loanTerm;
            loan.userId = this.userId;
            loan.status = this.status;
            loan.amountToBePaid = this.amountToBePaid;
            loan.interestRate = this.interestRate;
            loan.loanType = this.loanType;

            return loan;
        }
    }
}

