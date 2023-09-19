package com.example.loansystem.service;

import com.example.loansystem.dto.LoanRequest;
import com.example.loansystem.exceptions.*;
import com.example.loansystem.factory.LoanFactory;
import com.example.loansystem.factory.PersonalLoan;
import com.example.loansystem.factory.loaninterfaces.LoanTypeInfo;
import com.example.loansystem.model.*;
import com.example.loansystem.repository.EMIRepository;
import com.example.loansystem.repository.LoanRepository;
import com.example.loansystem.repository.UserRepository;
import com.example.loansystem.strategy.FixedEMIStrategy;
import com.example.loansystem.strategy.RepaymentStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EMIRepository emiRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateLoan() {
        // Mocked data
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setUserId(1L);
        loanRequest.setLoanType(LoanType.PERSONAL);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Test the createLoan method
        loanService.createLoan(loanRequest);

        // Verify that save method was called on loanRepository
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testCreateLoanUserNotFound() {
        // Create a loan request with a non-existing user ID
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setUserId(999L); // An ID for a non-existing user
        loanRequest.setLoanType(LoanType.PERSONAL);
        loanRequest.setAmountRequired(1000.0);
        loanRequest.setLoanTerm(12);

        when(userRepository.findById(loanRequest.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            loanService.createLoan(loanRequest);
        });
    }

    @Test
    public void testApproveLoan() {
        // Mocked data
        Long loanId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUserRole(UserRole.ADMIN);

        Loan loan = new Loan();
        loan.setStatus(LoanStatus.PENDING);
        loan.setLoanType(LoanType.PERSONAL);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        when(emiRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Test the approveLoan method
        loanService.approveLoan(loanId, userId);

        // Verify that save method was called on loanRepository and emiRepository
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(emiRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testApproveLoanUserNotFound() {
        Long loanId = 1L; // Specify an existing loan ID
        Long userId = 999L; // An ID for a non-existing user

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            loanService.approveLoan(loanId, userId);
        });
    }

    @Test
    public void testApproveLoanUserNotAdmin() {
        Long loanId = 1L; // Specify an existing loan ID
        Long userId = 2L; // An ID for a non-admin user

        User nonAdminUser = new User();
        nonAdminUser.setId(userId);
        nonAdminUser.setUserRole(UserRole.BORROWER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(nonAdminUser));

        assertThrows(InvalidUserAuthority.class, () -> {
            loanService.approveLoan(loanId, userId);
        });
    }

    @Test
    public void testApproveLoanLoanNotFound() {
        Long loanId = 999L; // An ID for a non-existing loan
        Long userId = 1L; // Specify an existing user ID

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserRole(UserRole.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> {
            loanService.approveLoan(loanId, userId);
        });
    }

    @Test
    public void testGetLoansForUser() {
        // Mocked data
        Long userId = 1L;
        LoanStatus status = LoanStatus.PENDING;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(loanRepository.findByUserIdAndStatus(userId, status)).thenReturn(new ArrayList<>());

        // Test the getLoansForUser method
        List<Loan> loans = loanService.getLoansForUser(userId, status);

        // Verify that findByUserIdAndStatus method was called on loanRepository
        verify(loanRepository, times(1)).findByUserIdAndStatus(userId, status);
    }

    @Test
    public void testGetLoansForUserUserNotFound() {
        Long userId = 999L; // An ID for a non-existing user
        LoanStatus status = LoanStatus.PENDING; // Specify a loan status

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            loanService.getLoansForUser(userId, status);
        });
    }


    @Test
    public void testGetLoansForAdminSuccess() {
        Long userId = 1L; // Provide the ID of an admin user
        LoanStatus status = LoanStatus.PENDING; // Specify a loan status

        User adminUser = new User();
        adminUser.setUserRole(UserRole.ADMIN);

        List<Loan> expectedLoans = new ArrayList<>();
        // Populate the expectedLoans list with some sample loans

        when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));
        when(loanRepository.findAllByStatus(status)).thenReturn(expectedLoans);

        List<Loan> actualLoans = loanService.getLoansForAdmin(userId, status);

        assertEquals(expectedLoans, actualLoans);
    }

    @Test
    public void testGetLoansForAdminUserNotFound() {
        Long userId = 123L; // Provide a non-existent user ID
        LoanStatus status = null; // Any status

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> loanService.getLoansForAdmin(userId, status));
    }

    @Test
    public void testGetLoansForAdminNotAdmin() {
        Long userId = 1L; // Provide the ID of a non-admin user
        LoanStatus status = null; // Any status

        User user = new User();
        user.setUserRole(UserRole.BORROWER); // Set user role to BORROWER

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidUserAuthority.class, () -> loanService.getLoansForAdmin(userId, status));
    }

    @Test
    public void testRepayLoan() {
        // Mocked data
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        Loan loan = new Loan();
        loan.setStatus(LoanStatus.PENDING);

        EMI emi1 = new EMI();
        emi1.setAmount(50.0);
        EMI emi2 = new EMI();
        emi2.setAmount(60.0);

        List<EMI> emiList = new ArrayList<>();
        emiList.add(emi1);
        emiList.add(emi2);

        when(loanRepository.findByIdAndStatus(loanId, LoanStatus.PENDING)).thenReturn(Optional.of(loan));
        when(emiRepository.findByLoanIdAndEmiStatusOrderByDueDateAsc(loanId, EMIStatus.PENDING)).thenReturn(emiList);

        // Test the repayLoan method
        loanService.repayLoan(loanId, repaymentAmount);

        // Verify that save method was called on emiRepository and loanRepository
        verify(emiRepository, times(2)).save(any(EMI.class));
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testRepayLoanNoPendingEMIs() {
        // Mocked data
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        Loan loan = new Loan();
        loan.setStatus(LoanStatus.PENDING);

        when(loanRepository.findByIdAndStatus(loanId, LoanStatus.PENDING)).thenReturn(Optional.of(loan));
        when(emiRepository.findByLoanIdAndEmiStatusOrderByDueDateAsc(loanId, EMIStatus.PENDING)).thenReturn(new ArrayList<>());

        // Test the repayLoan method with no pending EMIs
        try {
            loanService.repayLoan(loanId, repaymentAmount);
            // If no exception is thrown, fail the test
            Assertions.fail("Expected EMINotFoundException to be thrown.");
        } catch (EMINotFoundException e) {
            // Verify that EMINotFoundException was thrown as expected
            assertEquals("No pending EMIs for this loan does not exists: " + loanId, e.getMessage());
        }
    }

    @Test
    public void testRepayLoan_LoanNotFound() throws Exception {
        // Prepare loanId and repayment amount
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        // Mock the loanRepository to return an empty Optional, simulating a LoanNotFoundException
        when(loanRepository.findByIdAndStatus(eq(loanId), eq(LoanStatus.PENDING))).thenReturn(Optional.empty());

        // Perform the repayLoan method and expect a LoanNotFoundException
        try {
            loanService.repayLoan(loanId, repaymentAmount);
            Assertions.fail("Expected LoanNotFoundException, but no exception was thrown.");
        } catch (LoanNotFoundException e) {
            assertEquals("No Pending Loan Found for: " + loanId, e.getMessage());
        }
    }

    @Test
    public void testRepayLoan_EMINotFound() throws Exception {
        // Prepare loanId and repayment amount
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        // Mock the loanRepository to return a loan
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.PENDING);
        when(loanRepository.findByIdAndStatus(eq(loanId), eq(LoanStatus.PENDING))).thenReturn(Optional.of(loan));

        // Mock the emiRepository to return an empty list, simulating an EMINotFoundException
        when(emiRepository.findByLoanIdAndEmiStatusOrderByDueDateAsc(eq(loanId), eq(EMIStatus.PENDING)))
                .thenReturn(new ArrayList<>());

        // Perform the repayLoan method and expect an EMINotFoundException
        try {
            loanService.repayLoan(loanId, repaymentAmount);
            Assertions.fail("Expected EMINotFoundException, but no exception was thrown.");
        } catch (EMINotFoundException e) {
            assertEquals("No pending EMIs for this loan does not exists: " + loanId, e.getMessage());
        }
    }

    @Test
    public void testRepayLoan_InvalidRepaymentAmount() throws Exception {
        // Prepare loanId and repayment amount
        Long loanId = 1L;
        double repaymentAmount = 50.0;

        // Prepare a list of pending EMIs
        List<EMI> emiList = new ArrayList<>();
        EMI emi1 = new EMI();
        emi1.setAmount(60.0); // EMI amount is greater than repaymentAmount
        emiList.add(emi1);

        // Mock the loanRepository to return a loan
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.PENDING);
        when(loanRepository.findByIdAndStatus(eq(loanId), eq(LoanStatus.PENDING))).thenReturn(Optional.of(loan));

        // Mock the emiRepository to return the list of pending EMIs
        when(emiRepository.findByLoanIdAndEmiStatusOrderByDueDateAsc(eq(loanId), eq(EMIStatus.PENDING)))
                .thenReturn(emiList);

        // Perform the repayLoan method and expect a RepaymentAmountException
        try {
            loanService.repayLoan(loanId, repaymentAmount);
            Assertions.fail("Expected RepaymentAmountException, but no exception was thrown.");
        } catch (RepaymentAmountException e) {
            assertEquals("Repayment Amount should be greater than or equal to EMI Amount", e.getMessage());
        }
    }

    @Test
    public void testRepayLoan_LoanAlreadyPaid() throws Exception {
        // Prepare loanId and repayment amount
        Long loanId = 1L;
        double repaymentAmount = 100.0;

        // Prepare a list of paid EMIs
        List<EMI> emiList = new ArrayList<>();
        EMI emi1 = new EMI();
        emi1.setLoanId(loanId);
        emi1.setAmount(50.0);
        emi1.setEmiStatus(EMIStatus.PAID);
        emiList.add(emi1);

        // Mock the loanRepository to return a loan with PAID status
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.PAID);
        when(loanRepository.findByIdAndStatus(eq(loanId), eq(LoanStatus.PENDING))).thenReturn(Optional.empty());

        // Mock the emiRepository to return the list of paid EMIs
        when(emiRepository.findByLoanIdAndEmiStatusOrderByDueDateAsc(eq(loanId), eq(EMIStatus.PENDING)))
                .thenReturn(emiList);

        // Perform the repayLoan method and expect a LoanAlreadyPaidException
        try {
            loanService.repayLoan(loanId, repaymentAmount);
            Assertions.fail("Expected LoanNotFoundException, but no exception was thrown.");
        } catch (LoanNotFoundException e) {
            assertEquals("No Pending Loan Found for: "+loanId, e.getMessage());
        }
    }

    @Test
    public void testRepayLoan_PartialRepayment() throws Exception {
        // Prepare loanId and partial repayment amount
        Long loanId = 1L;
        double repaymentAmount = 50.0;

        // Prepare a list of pending EMIs
        List<EMI> emiList = new ArrayList<>();
        EMI emi1 = new EMI();
        emi1.setAmount(60.0);// EMI amount is greater than repaymentAmount
        emi1.setLoanId(loanId);
        emi1.setEmiStatus(EMIStatus.PENDING);
        EMI emi2 = new EMI();
        emi2.setAmount(70.0); // EMI amount is greater than repaymentAmount
        emi2.setLoanId(loanId);
        emi2.setEmiStatus(EMIStatus.PENDING);
        emiList.add(emi1);
        emiList.add(emi2);

        // Mock the loanRepository to return a loan
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setStatus(LoanStatus.PENDING);
        when(loanRepository.findByIdAndStatus(eq(loanId), eq(LoanStatus.PENDING))).thenReturn(Optional.of(loan));

        // Mock the emiRepository to return the list of pending EMIs
        when(emiRepository.findByLoanIdAndEmiStatusOrderByDueDateAsc(eq(loanId), eq(EMIStatus.PENDING)))
                .thenReturn(emiList);

        // Perform the repayLoan method
        try {
            loanService.repayLoan(loanId, repaymentAmount);
            Assertions.fail("Expected RepaymentAmountException, but no exception was thrown.");
        } catch (RepaymentAmountException e) {
            assertEquals("Repayment Amount should be greater than or equal to EMI Amount", e.getMessage());
        }
    }
}
