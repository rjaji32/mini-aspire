package com.example.loansystem.repository;

import com.example.loansystem.model.EMI;
import com.example.loansystem.model.EMIStatus;
import com.example.loansystem.model.Loan;
import com.example.loansystem.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EMIRepository extends JpaRepository<EMI, Long> {
    List<EMI> findByUserId(Long userId);
    List<EMI> findByLoanIdAndEmiStatusOrderByDueDateAsc(Long loanId, EMIStatus emiStatus);
    List<EMI> findByLoanId(Long loanId);
}
