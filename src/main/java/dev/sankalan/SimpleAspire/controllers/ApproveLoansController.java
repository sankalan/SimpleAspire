package dev.sankalan.SimpleAspire.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.UserRole;
import dev.sankalan.SimpleAspire.services.LoansService;
import dev.sankalan.SimpleAspire.session.SessionContext;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;

@RestController
public class ApproveLoansController {

	@Autowired
	private LoansService loansService;
	@Autowired
	private SessionContext sessionContext;
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@PostMapping("/loan/{id}/approve")
	public void approveLoan(@PathVariable String id) {
		if(sessionContext.getUser().getRole() != UserRole.ADMIN) {
			log.error("User is not authorised to approve loan, user: " + sessionContext.getUser().getUsername());
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorMessages.NOT_AUTHORISED);
		}
		try {
			loansService.approveLoan(id);
		}catch(ResponseStatusException ex) {
			log.debug("Exception thrown from downstream");
			throw ex;
		}catch(Exception ex) {
			log.error("Unexpected exception occurred while getting loans.");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
		}
		
	}
}
