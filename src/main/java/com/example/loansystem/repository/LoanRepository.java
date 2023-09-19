package com.example.loansystem.repository;

import com.example.loansystem.model.Loan;
import com.example.loansystem.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

    List<Loan> findAllByStatus(LoanStatus status);
    List<Loan> findAll();
    Optional<Loan> findById(Long loanId);
    Optional<Loan> findByIdAndStatus(Long loanId, LoanStatus status);
}
