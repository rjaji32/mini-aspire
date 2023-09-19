package com.example.loansystem.controller;

import com.example.loansystem.controller.LoanController;
import com.example.loansystem.dto.LoanRequest;
import com.example.loansystem.dto.RepaymentRequest;
import com.example.loansystem.exceptions.*;
import com.example.loansystem.model.Loan;
import com.example.loansystem.model.LoanStatus;
import com.example.loansystem.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LoanControllerTest {

    @InjectMocks
    private LoanController loanController;

    @Mock
    private LoanService loanService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateLoan() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setUserId(1L);

        doNothing().when(loanService).createLoan(loanRequest);

        ResponseEntity<String> response = loanController.createLoan(loanRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Loan request created successfully", response.getBody());
    }

    @Test
    public void testCreateLoan_UserNotFound() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setUserId(1L);

        doThrow((new UserNotFoundException("User not found"))).when(loanService).createLoan(loanRequest);

        ResponseEntity<String> response = loanController.createLoan(loanRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testApproveLoan() {
        Long loanId = 1L;
        Long userId = 2L;

        doNothing().when(loanService).approveLoan(loanId, userId);

        ResponseEntity<String> response = loanController.approveLoan(loanId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Loan approved successfully", response.getBody());
    }

    @Test
    public void testApproveLoan_LoanNotFound() {
        Long loanId = 1L;
        Long userId = 2L;

        doThrow(new LoanNotFoundException("Loan not found")).when(loanService).approveLoan(loanId, userId);

        ResponseEntity<String> response = loanController.approveLoan(loanId, userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetMyLoans() {
        Long userId = 1L;
        LoanStatus status = LoanStatus.PENDING;

        List<Loan> loans = new ArrayList<>();
        when(loanService.getLoansForUser(userId, status)).thenReturn(loans);

        ResponseEntity<List<Loan>> response = loanController.getMyLoans(status, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(loans, response.getBody());
    }

    @Test
    public void testGetUsersLoans() {
        Long userId = 1L;
        LoanStatus status = LoanStatus.PENDING;

        List<Loan> loans = new ArrayList<>();
        when(loanService.getLoansForAdmin(userId, status)).thenReturn(loans);

        ResponseEntity<List<Loan>> response = loanController.getUsersLoans(status, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(loans, response.getBody());
    }

    @Test
    public void testRepayLoan() {
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        doNothing().when(loanService).repayLoan(loanId, repaymentAmount);

        RepaymentRequest repaymentRequest = new RepaymentRequest();
        repaymentRequest.setRepaymentAmount(repaymentAmount);

        ResponseEntity<String> response = loanController.repayLoan(loanId, repaymentRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Repayment successful", response.getBody());
    }

    @Test
    public void testRepayLoan_LoanNotFound() {
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        doThrow(new LoanNotFoundException("Loan not found")).when(loanService).repayLoan(loanId, repaymentAmount);

        RepaymentRequest repaymentRequest = new RepaymentRequest();
        repaymentRequest.setRepaymentAmount(repaymentAmount);

        ResponseEntity<String> response = loanController.repayLoan(loanId, repaymentRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testRepayLoan_InvalidRepaymentAmount() {
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        doThrow(new InvalidRepaymentAmountException("Invalid amount")).when(loanService).repayLoan(loanId, repaymentAmount);

        RepaymentRequest repaymentRequest = new RepaymentRequest();
        repaymentRequest.setRepaymentAmount(repaymentAmount);

        ResponseEntity<String> response = loanController.repayLoan(loanId, repaymentRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid repayment amount", response.getBody());
    }

    @Test
    public void testRepayLoan_LoanAlreadyPaid() {
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        doThrow(new LoanAlreadyPaidException("Loan already paid")).when(loanService).repayLoan(loanId, repaymentAmount);

        RepaymentRequest repaymentRequest = new RepaymentRequest();
        repaymentRequest.setRepaymentAmount(repaymentAmount);

        ResponseEntity<String> response = loanController.repayLoan(loanId, repaymentRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Loan is already paid", response.getBody());
    }

    @Test
    public void testRepayLoan_EMINotFound() {
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        doThrow(new EMINotFoundException("EMI not found")).when(loanService).repayLoan(loanId, repaymentAmount);

        RepaymentRequest repaymentRequest = new RepaymentRequest();
        repaymentRequest.setRepaymentAmount(repaymentAmount);

        ResponseEntity<String> response = loanController.repayLoan(loanId, repaymentRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testRepayLoanRepaymentAmountLessThanEMI() {
        // Create a repayment request with an amount less than the first pending EMI
        Long loanId = 1L;
        RepaymentRequest repaymentRequest = new RepaymentRequest();
        double repaymentAmount = 100000;
        repaymentRequest.setRepaymentAmount(repaymentAmount); // Less than the first pending EMI amount
        doThrow(new RepaymentAmountException("Repayment Amount should be greater than or equal to EMI Amount")).when(loanService).repayLoan(loanId, repaymentAmount);
        ResponseEntity<String> response = loanController.repayLoan(loanId, repaymentRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Repayment Amount should be greater than or equal to EMI Amount",response.getBody());
    }

}

