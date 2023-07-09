package dev.sankalan.SimpleAspire.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import dev.sankalan.SimpleAspire.utils.LoanUtil;

/**
 * Service to handle Loan operations: GET, CREATE, APPROVE
 */
@Component
public class LoansService {

	@Autowired
	LoanRepository loanRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	LoanUtil loanUtil;
	
	private final Logger log = LogManager.getLogger(getClass());

	/**
	 * Get all loans
	 * @return List<Loan>
	 */
	public List<Loan> getAllLoans() {
		log.info("Service: getting all loans: ");
		List<Loan> loans = new ArrayList<Loan>();
		loanRepo.findAll().forEach(loan -> loans.add(loan));
		return loans;
	}
	
	/**
	 * Get all loans of an user
	 * @param username
	 * @return List<Loan>
	 */
	public List<Loan> getLoanByUser(String username) {
		log.info("Service: getting all loans for an user");
		User user = userRepo.findByUsername(username);
		if(user == null) {
			log.error("Cannot find user");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.USER_NOT_FOUND);
		}
		log.info("Service: getting all loans, for user: " + username);
		return loanRepo.findByOwnerId(user.getUserId());
	}
	
	/**
	 * Create a loan for the user
	 * @param loan
	 * @param ownerName
	 */
	public void createLoan(Loan loan, String ownerName) {
		if(loan.getAmount()<=0 || loan.getTerm()<=0) {
			log.error("Invalid input, amount:" + loan.getAmount() + ", term: " + loan.getTerm());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_INPUT_FOR_CREATE);
		}
		/* Get user */
		User user = userRepo.findByUsername(ownerName);
		if(user == null) {
			log.error("Cannot find user");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.USER_NOT_FOUND);
		}
		loan.setOwnerId(user.getUserId());
		loan.setRepayments(generateRepayments(loan));
		loanRepo.save(loan);
	}
	
	/* Creates Loan repayment schedule based on term and amount*/
	private List<LoanRepaymentSchedule> generateRepayments(Loan loan) {
		double installmentAmount = loanUtil.getFormattedPrice(loan.getAmount()/loan.getTerm());
		Date lastDate = loan.getDate();
		List<LoanRepaymentSchedule> repayments = new ArrayList<LoanRepaymentSchedule>();
		
		for(int i=0; i<loan.getTerm(); i++) {
			Date nextDate = loanUtil.getNextDate(lastDate);
			LoanRepaymentSchedule repayment = new LoanRepaymentSchedule(installmentAmount, nextDate);
			repayments.add(repayment);
			lastDate = nextDate;
		}
		
		/* Adjust rounding errors*/
		double roundUpAdjustment = loanUtil.getFormattedPrice(installmentAmount + loan.getAmount() - installmentAmount*loan.getTerm());
		
		repayments.get(loan.getTerm()-1).setAmount(roundUpAdjustment);
		repayments.get(loan.getTerm()-1).setOutstanding(roundUpAdjustment);
		return repayments;
	}

	/**
	 * Approves a loan
	 * @param id
	 */
	public void approveLoan(int id) {
		Optional<Loan> loan = loanRepo.findById(id);
		
		if(!loan.isPresent()) {
			log.error("Cannot find loan");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.LOAN_NOT_FOUND);
		}else if(loan.get().getStatus() != LoanStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, ErrorMessages.NOT_ELIGIBLE_FOR_APPROVAL);
		}
		
		loan.get().approveLoan();
		loanRepo.save(loan.get());
	}

}
