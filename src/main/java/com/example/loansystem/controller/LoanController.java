package com.example.loansystem.controller;

import com.example.loansystem.dto.LoanRequest;
import com.example.loansystem.dto.RepaymentRequest;
import com.example.loansystem.exceptions.*;
import com.example.loansystem.model.Loan;
import com.example.loansystem.model.LoanStatus;
import com.example.loansystem.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aspire/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/")
    public ResponseEntity<String> createLoan(@RequestBody LoanRequest loanRequest) {
        try {
            loanService.createLoan(loanRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Loan request created successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{loanId}/approvals")
    public ResponseEntity<String> approveLoan(@PathVariable Long loanId, @RequestBody Long userId) {
        try {
            loanService.approveLoan(loanId, userId);
            return ResponseEntity.ok("Loan approved successfully");
        } catch (LoanNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Loan>> getMyLoans(
            @RequestParam(value = "status", required = false) LoanStatus status,
            @RequestParam(value = "userId", required = true) Long userId
    ) {
        List<Loan> loans = loanService.getLoansForUser(userId,status);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/all-loans")
    public ResponseEntity<List<Loan>> getUsersLoans(
            @RequestParam(value = "status", required = false) LoanStatus status,
            @RequestParam(value = "userId", required = true) Long userId
    ) {
        List<Loan> loans = loanService.getLoansForAdmin(userId,status);
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/{loanId}/repayments")
    public ResponseEntity<String> repayLoan(@PathVariable Long loanId, @RequestBody RepaymentRequest repaymentRequest) {
        try {
            loanService.repayLoan(loanId, repaymentRequest.getRepaymentAmount());
            return ResponseEntity.ok("Repayment successful");
        } catch (LoanNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidRepaymentAmountException e) {
            return ResponseEntity.badRequest().body("Invalid repayment amount");
        } catch (LoanAlreadyPaidException e) {
            return ResponseEntity.badRequest().body("Loan is already paid");
        } catch (EMINotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RepaymentAmountException e) {
            return ResponseEntity.badRequest().body("Repayment Amount should be greater than or equal to EMI Amount");
        }
    }
}