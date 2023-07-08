package dev.sankalan.SimpleAspire.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.models.LoanRepaymentSchedule;
import dev.sankalan.SimpleAspire.models.LoanStatus;
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.repositories.LoanRepository;
import dev.sankalan.SimpleAspire.repositories.UserRepository;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;

@Component
public class LoansService {

	@Autowired
	LoanRepository loanRepo;
	@Autowired
	UserRepository userRepo;
	
	private final Logger log = LogManager.getLogger(getClass());
	
	public List<Loan> getAllLoans() {
		System.out.println("Service: all loans: " + loanRepo.getAll());
		return loanRepo.getAll();
	}
	
	public List<Loan> getLoanByUser(String username) {
		User user = userRepo.getUserByName(username);
		if(user == null) {
			log.error("Cannot find user");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.USER_NOT_FOUND);
		}
		return loanRepo.getByUserId(user.getUserId());
	}
	
	public void createLoan(Loan loan, String ownerName) {
		User user = userRepo.getUserByName(ownerName);
		if(user == null) {
			log.error("Cannot find user");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.USER_NOT_FOUND);
		}
		loan.setOwnerId(user.getUserId());
		loan.setRepayments(generateRepayments(loan));
		loanRepo.add(loan);
	}
	
	private List<LoanRepaymentSchedule> generateRepayments(Loan loan) {
		double installmentAmount = getFormattedPrice(loan.getAmount()/loan.getTerm());
		Date lastDate = loan.getDate();
		List<LoanRepaymentSchedule> repayments = new ArrayList<LoanRepaymentSchedule>();
		
		for(int i=0; i<loan.getTerm(); i++) {
			Date nextDate = getNextDate(lastDate);
			LoanRepaymentSchedule repayment = new LoanRepaymentSchedule(installmentAmount, nextDate);
			repayments.add(repayment);
			lastDate = nextDate;
		}
		
		double roundUpAdjustment = getFormattedPrice(installmentAmount + loan.getAmount() - installmentAmount*loan.getTerm());
		
		repayments.get(loan.getTerm()-1).setAmount(roundUpAdjustment);
		repayments.get(loan.getTerm()-1).setOutstanding(roundUpAdjustment);
		return repayments;
	}
	
	private double getFormattedPrice(double price) {
		DecimalFormat df=new DecimalFormat("0.00");
		return (double) Double.parseDouble(df.format(price));
	}

	private Date getNextDate(Date lastDate) {
		final int milliSecondsInOneWeek = 1000 * 60 * 60 * 24 * 7; //each installment is 7 days apart
		return new Date(lastDate.getTime() + milliSecondsInOneWeek);
	}

	public void approveLoan(String id) {
		Loan loan = loanRepo.getById(id);
		
		if(loan == null) {
			log.error("Cannot find loan");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.LOAN_NOT_FOUND);
		}else if(loan.getStatus() != LoanStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessages.NOT_ELIGIBLE_FOR_APPROVAL);
		}
		
		loan.approveLoan();
	}

}
