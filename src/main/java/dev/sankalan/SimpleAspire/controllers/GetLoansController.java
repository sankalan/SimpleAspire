package dev.sankalan.SimpleAspire.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.models.SessionContext;
import dev.sankalan.SimpleAspire.models.UserRole;
import dev.sankalan.SimpleAspire.services.LoansService;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;

@RestController
public class GetLoansController {
	
	@Autowired
	private LoansService getLoansService;
	@Autowired
	private SessionContext sessionContext;
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@GetMapping("/loans")
	public List<Loan> getLoans() {
		System.out.println("Get all loans");
		
		try {
			if(sessionContext.getUser().getRole() == UserRole.ADMIN) {
				return getLoansService.getAllLoans();
			} else if(sessionContext.getUser().getRole() == UserRole.USER) {
				return getLoansService.getLoanByUser(sessionContext.getUser().getUsername());
			}
		}catch(ResponseStatusException ex) {
			log.debug("Exception thrown from downstream");
			throw ex;
		}catch(Exception ex) {
			log.error("Unexpected exception occurred while getting loans. Exception: " + ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
		}
		log.error("user invalid. Not valid user role: " + sessionContext.getUser().getRole());
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.INVAILD_USER);
	}
}
