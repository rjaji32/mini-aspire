package com.example.loansystem.service;

import com.example.loansystem.dto.LoanRequest;
import com.example.loansystem.exceptions.*;
import com.example.loansystem.factory.LoanFactory;
import com.example.loansystem.factory.loaninterfaces.LoanTypeInfo;
import com.example.loansystem.model.*;
import com.example.loansystem.repository.EMIRepository;
import com.example.loansystem.repository.LoanRepository;
import com.example.loansystem.repository.UserRepository;
import com.example.loansystem.strategy.FixedEMIStrategy;
import com.example.loansystem.strategy.RepaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EMIRepository emiRepository;

    public void createLoan(LoanRequest loanRequest) {
        User user = userRepository.findById(loanRequest.getUserId()).orElseThrow(() ->new UserNotFoundException("User not found with given email"));
        LoanTypeInfo loanTypeInfo = LoanFactory.getInterestRate(loanRequest.getLoanType());
        double interestRate = loanTypeInfo.getInterestRate();
        double amountToBePaid = loanTypeInfo.getInterest(loanRequest.getAmountRequired(), interestRate, loanRequest.getLoanTerm());
        Loan loan = Loan.builder()
                .amountRequired(loanRequest.getAmountRequired())
                .loanTerm(loanRequest.getLoanTerm())
                .userId(user.getId())
                .status(LoanStatus.PENDING)
                .amountToBePaid(amountToBePaid)
                .interestRate(interestRate)
                .loanType(loanRequest.getLoanType())
                .build();
        loanRepository.save(loan);
    }

    @Transactional
    public void approveLoan(Long loanId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found with given email"));
        if (user.getUserRole() != UserRole.ADMIN) {
            throw new InvalidUserAuthority("You dont have access for this feature");
        }
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan Not Found for: "+loanId));

        loan.setStatus(LoanStatus.APPROVED);
        loan.setStartDate(new Date());

        //When Loan is approved, then only emis need to be created
        RepaymentStrategy repaymentStrategy = getRepaymentStrategyBasedOnLoanType((loan.getLoanType()));
        List<EMI> emis = repaymentStrategy.generateEMIs(loan);

        loanRepository.save(loan);
        emiRepository.saveAll(emis);

    }

    public List<Loan> getLoansForUser(Long userId, LoanStatus status) {
        //Validating user
        userRepository.findById(userId).orElseThrow(() ->new UserNotFoundException("User not found with given email"));
        if(status == null) {
            return loanRepository.findByUserId(userId);
        }
        return loanRepository.findByUserIdAndStatus(userId, status);
    }

    public List<Loan> getLoansForAdmin(Long userId, LoanStatus status) {
        User user = userRepository.findById(userId).orElseThrow(() ->new UserNotFoundException("User not found with given email"));
        if (user.getUserRole() != UserRole.ADMIN) {
            throw new InvalidUserAuthority("You dont have access for this feature");
        }
        if(status == null) {
            return loanRepository.findAll();
        }
        return loanRepository.findAllByStatus(status);
    }

    @Transactional
    public void repayLoan(Long loanId, double repaymentAmount) {
        Loan loan = loanRepository.findByIdAndStatus(loanId, LoanStatus.PENDING)
                .orElseThrow(() -> new LoanNotFoundException("No Pending Loan Found for: " + loanId));
        List<EMI> emiList = emiRepository.findByLoanIdAndEmiStatusOrderByDueDateAsc(loanId, EMIStatus.PENDING);
        if (CollectionUtils.isEmpty(emiList)) {
            throw new EMINotFoundException("No pending EMIs for this loan does not exists: "+loanId);
        }
        //If repayment amount is less than first pending emi then throw error
        EMI firstPendingEMI = emiList.get(0);
        if (firstPendingEMI.getAmount()>repaymentAmount) {
            throw new RepaymentAmountException("Repayment Amount should be greater than or equal to EMI Amount");
        }


        for (EMI emi : emiList) {
            if (repaymentAmount >= emi.getAmount()) {
                emi.setEmiStatus(EMIStatus.PAID);
                repaymentAmount -= emi.getAmount();
            } else {
                //if amount is less than emi amount then less that amount from emi and keep emi in pending status
                emi.setAmount(emi.getAmount()-repaymentAmount);
                repaymentAmount=0;
            }
            emiRepository.save(emi);
        }
        List<EMI> allEmisListForLoan = emiRepository.findByLoanId(loanId);
        if (allEmisListForLoan.stream().allMatch(emi -> emi.getEmiStatus() == EMIStatus.PAID)) {
            loan.setStatus(LoanStatus.PAID);
        }
        loanRepository.save(loan);
    }

    private RepaymentStrategy getRepaymentStrategyBasedOnLoanType(LoanType loanType) {
        switch (loanType) {
            case PERSONAL:
                return new FixedEMIStrategy();
            default:
                throw new IllegalArgumentException("Unsupported loan type: " + loanType);
        }
    }
}
