package dev.sankalan.SimpleAspire.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Repayment;
import dev.sankalan.SimpleAspire.services.LoanRepaymentService;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;

@RestController
public class RepaymentController {
	@Autowired
	private LoanRepaymentService loanRepaymentService;
	private final Logger log = LogManager.getLogger(getClass());
	
	@PostMapping("/loan/{id}/pay")
	public void repayLoan(@PathVariable int id, @RequestBody Repayment repayment) {
		try {
			loanRepaymentService.addRepayment(id, repayment);
		}catch(ResponseStatusException ex) {
			log.debug("Exception thrown from downstream");
			throw ex;
		}catch(Exception ex) {
			log.error("Unexpected exception occurred while repaying loans.", ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
		}
	}

}
