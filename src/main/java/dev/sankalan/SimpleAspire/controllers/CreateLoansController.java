package dev.sankalan.SimpleAspire.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.services.LoansService;
import dev.sankalan.SimpleAspire.session.SessionContext;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class CreateLoansController {
	
	@Autowired
	private LoansService loansService;
	@Autowired
	private SessionContext sessionContext;
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@PostMapping("/loan")
	public void createLoan(@RequestBody final Loan loan, HttpServletResponse response) {
		log.info("Creating loan: " + loan);
		try {
			loansService.createLoan(loan, sessionContext.getUser().getUsername());
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader(HttpHeaders.LOCATION, loan.getId());
		}catch(ResponseStatusException ex) {
			log.debug("Exception thrown from downstream while creating loan");
			throw ex;
		}catch(Exception ex) {
			log.error("Unexpected exception occurred while creating loan.");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
		}
	}

}
