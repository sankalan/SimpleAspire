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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.models.SessionContext;
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.services.LoansService;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class CreateLoansControllerTests {

	@InjectMocks
	CreateLoansController createLoansController;
	
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
	public void CreateLoan_happyCase() {
		when(user.getUsername()).thenReturn(TestConstants.USERNAME);
		when(sessionContext.getUser()).thenReturn(user);
		when(loan.getId()).thenReturn(TestConstants.LOAN_ID);
		
		createLoansController.createLoan(loan, response);
		verify(loansService, times(1)).createLoan(loan, TestConstants.USERNAME);
		verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
		verify(response, times(1)).setHeader(HttpHeaders.LOCATION, TestConstants.LOAN_ID+"");
	}
	
	@Test
	public void CreateLoan_relaysServiceEx() {
		ResponseStatusException mockedEx = mock(ResponseStatusException.class);
		when(user.getUsername()).thenReturn(TestConstants.USERNAME);
		when(sessionContext.getUser()).thenReturn(user);
		doThrow(mockedEx).when(loansService).createLoan(loan, TestConstants.USERNAME);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class, () -> createLoansController.createLoan(loan, response));
		assertEquals(mockedEx, actualEx);
	}
	
	@Test
	public void CreateLoan_handlesUnexpectedEx() {
		NullPointerException mockedEx = mock(NullPointerException.class);
		when(user.getUsername()).thenReturn(TestConstants.USERNAME);
		when(sessionContext.getUser()).thenReturn(user);
		doThrow(mockedEx).when(loansService).createLoan(loan, TestConstants.USERNAME);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class, () -> createLoansController.createLoan(loan, response));
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualEx.getStatusCode());
		assertEquals(ErrorMessages.SERVER_ERROR, actualEx.getReason());
	}
}
