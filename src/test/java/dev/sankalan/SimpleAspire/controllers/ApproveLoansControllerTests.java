package dev.sankalan.SimpleAspire.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.models.SessionContext;
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.models.UserRole;
import dev.sankalan.SimpleAspire.services.LoansService;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class ApproveLoansControllerTests {
	@InjectMocks
	ApproveLoansController approveLoansController;
	
	@Mock
	private LoansService loansService;
	@Mock
	private SessionContext sessionContext;
	@Mock
	private Loan loan;
	@Mock
	private HttpServletResponse response;
	@Mock
	private User user;
	
	@Test
	public void approveLoan_happyCase() {
		when(user.getRole()).thenReturn(UserRole.ADMIN);
		when(sessionContext.getUser()).thenReturn(user);
		
		approveLoansController.approveLoan(TestConstants.LOAN_ID);
		verify(loansService, times(1)).approveLoan(TestConstants.LOAN_ID);
	}
	
	@Test
	public void approveLoan_insufficientPrivilege() {
		when(user.getRole()).thenReturn(UserRole.USER);
		when(sessionContext.getUser()).thenReturn(user);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class, () -> approveLoansController.approveLoan(TestConstants.LOAN_ID));
		
		assertEquals(HttpStatus.FORBIDDEN, actualEx.getStatusCode());
		assertEquals(ErrorMessages.NOT_AUTHORISED, actualEx.getReason());
	}
	
	@Test
	public void approveLoan_relaysExpectedExceptions() {
		ResponseStatusException mockedEx = mock(ResponseStatusException.class);
		when(user.getRole()).thenReturn(UserRole.ADMIN);
		when(sessionContext.getUser()).thenReturn(user);
		doThrow(mockedEx).when(loansService).approveLoan(TestConstants.LOAN_ID);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class, () -> approveLoansController.approveLoan(TestConstants.LOAN_ID));
		
		assertEquals(mockedEx, actualEx);
	}
	
	@Test
	public void approveLoan_handlesUnexpectedExceptions() {
		NullPointerException mockedEx = mock(NullPointerException.class);
		when(user.getRole()).thenReturn(UserRole.ADMIN);
		when(sessionContext.getUser()).thenReturn(user);
		doThrow(mockedEx).when(loansService).approveLoan(TestConstants.LOAN_ID);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class, () -> approveLoansController.approveLoan(TestConstants.LOAN_ID));
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualEx.getStatusCode());
		assertEquals(ErrorMessages.SERVER_ERROR, actualEx.getReason());
	}
}
