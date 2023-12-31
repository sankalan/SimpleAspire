package dev.sankalan.SimpleAspire.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.models.LoanRepaymentSchedule;
import dev.sankalan.SimpleAspire.models.LoanRepaymentStatus;
import dev.sankalan.SimpleAspire.models.LoanStatus;
import dev.sankalan.SimpleAspire.models.Repayment;
import dev.sankalan.SimpleAspire.repositories.LoanRepository;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import dev.sankalan.SimpleAspire.utils.LoanUtil;

/**
 * Service to handle laon repayments
 */
@Component
public class LoanRepaymentService {
	@Autowired
	LoanRepository loanRepo;
	@Autowired
	LoanUtil loanUtil;
	
	private final Logger log = LogManager.getLogger(getClass());
	
	/**
	 * Processes a loan repayment
	 * @param id
	 * @param installment
	 */
	public void addRepayment(int id, Repayment installment) {
		Loan loan = loanRepo.findById(id).orElse(null);
		log.info("Repaying: " + installment.getAmount());
		
		/* Step 1: validate the loan object*/
		validateLoanStatus(loan);
		
		/*Step 2: Get and parse through each repayment schedule to find the PENDING ones*/
		List<LoanRepaymentSchedule> repayments = loan.getRepayments();
		
		for(int i=0; i<repayments.size(); i++) {
			LoanRepaymentSchedule repayment = repayments.get(i);
			if(repayment.getStatus() == LoanRepaymentStatus.PENDING) {
				/* installment amount need to be >= next due amount*/
				if(installment.getAmount() >= repayment.getOutstanding()) {
					log.info("Paying installment no: " + (i+1));
					/*Step 3: Complete the next installment */
					double excessAmount = installment.getAmount()-repayment.getOutstanding();
					repayment.makePayment();
					
					/* Adjust if there is any excess amount*/
					log.info("Excess amount: " + excessAmount);
					int j = i+1;
					
					while(excessAmount>0 && j<repayments.size()) {
						LoanRepaymentSchedule nextSchedule = repayments.get(j);
						if(excessAmount >= nextSchedule.getOutstanding()) {
							log.info("Paying installment no: " + (j+1));
							excessAmount = excessAmount - nextSchedule.getOutstanding();
							nextSchedule.makePayment();
						} else {
							log.info("Adjusting installment no: " + (j+1));
							nextSchedule.setOutstanding(loanUtil.getFormattedPrice(nextSchedule.getOutstanding() - excessAmount));
							excessAmount = 0;
						}
						j++;
					}
					log.info("Setting loan outstanding to be: " + (loan.getOutstanding()-installment.getAmount()));
					loan.setOutstanding(loanUtil.getFormattedPrice(loan.getOutstanding()-installment.getAmount()));
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.INSUFFICIENT_REPAYMENT);
				}
				break;
			}
		}
		
		/* If all repayments are PAID, complete the Loan*/
		if(repayments.get(repayments.size()-1).getStatus() == LoanRepaymentStatus.PAID) {
			loan.completeLoanPayment();
		}
		
		/* Save in DB */
		loanRepo.save(loan);
		
	}

	private void validateLoanStatus(Loan loan) {
		if(loan == null) {
			log.error("Cannot find loan");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.LOAN_NOT_FOUND);
		} else if(loan.getStatus() == LoanStatus.PENDING) {
			log.error("Cannot pay for PENDING loan");
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, ErrorMessages.NOT_ELIGIBLE_FOR_PAYMENT_PENDING);
		} else if(loan.getStatus() == LoanStatus.PAID) {
			log.error("Cannot pay for PAID loan");
			throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessages.NOT_ELIGIBLE_FOR_PAYMENT_PAID);
		}
	}
}
