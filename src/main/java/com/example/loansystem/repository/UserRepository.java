package com.example.loansystem.repository;

import com.example.loansystem.model.Loan;
import com.example.loansystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findById(Long userId);
}
