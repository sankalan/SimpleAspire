package dev.sankalan.SimpleAspire.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Repayment;
import dev.sankalan.SimpleAspire.services.LoanRepaymentService;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;

@ExtendWith(MockitoExtension.class)
public class RepaymentControllerTests {

	@InjectMocks
	RepaymentController repaymentController;
	
	@Mock
	private LoanRepaymentService service;
	@Mock
	private Repayment repayment;
	
	@Test
	public void repayLoan_happyCase() {
		repaymentController.repayLoan(TestConstants.LOAN_ID	, repayment);
		
		verify(service, times(1)).addRepayment(TestConstants.LOAN_ID, repayment);
	}
	
	@Test
	public void repayLoan_relaysExpectedException() {
		ResponseStatusException mockedEx = mock(ResponseStatusException.class);
		doThrow(mockedEx).when(service).addRepayment(TestConstants.LOAN_ID, repayment);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class, 
						() -> repaymentController.repayLoan(TestConstants.LOAN_ID, repayment));
		assertEquals(mockedEx, actualEx);
	}
	
	@Test
	public void repayLoan_handlesUnExpectedException() {
		NullPointerException mockedEx = mock(NullPointerException.class);
		doThrow(mockedEx).when(service).addRepayment(TestConstants.LOAN_ID, repayment);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class, 
						() -> repaymentController.repayLoan(TestConstants.LOAN_ID, repayment));
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualEx.getStatusCode());
		assertEquals(ErrorMessages.SERVER_ERROR, actualEx.getReason());
	}
}
