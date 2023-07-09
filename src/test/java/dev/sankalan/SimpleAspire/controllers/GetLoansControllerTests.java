package dev.sankalan.SimpleAspire.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.models.SessionContext;
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.models.UserRole;
import dev.sankalan.SimpleAspire.services.LoansService;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;

@ExtendWith(MockitoExtension.class)
public class GetLoansControllerTests {
	@InjectMocks
	private GetLoansController getLoansController;
	@Mock
	private LoansService getLoansService;
	@Mock
	private SessionContext sessionContext;
	@Mock
	private User user;
	@Mock
	private List<Loan> mockedLoans;
	
	@Test
	public void GetLoansTest_admin() {
		when(user.getRole()).thenReturn(UserRole.ADMIN);
		when(sessionContext.getUser()).thenReturn(user);
		when(getLoansService.getAllLoans()).thenReturn(mockedLoans);
		
		assertEquals(mockedLoans, getLoansController.getLoans());
	}
	
	@Test
	public void GetLoansTest_user() {
		when(user.getRole()).thenReturn(UserRole.USER);
		when(user.getUsername()).thenReturn(TestConstants.USERNAME);
		when(sessionContext.getUser()).thenReturn(user);
		when(getLoansService.getLoanByUser(TestConstants.USERNAME)).thenReturn(mockedLoans);
		
		assertEquals(mockedLoans, getLoansController.getLoans());
	}
	
	@Test
	public void GetLoansTest_passesExceptionFromService() {
		ResponseStatusException mockedEx = mock(ResponseStatusException.class);
		when(user.getRole()).thenReturn(UserRole.USER);
		when(user.getUsername()).thenReturn(TestConstants.USERNAME);
		when(sessionContext.getUser()).thenReturn(user);
		when(getLoansService.getLoanByUser(TestConstants.USERNAME))
				.thenThrow(mockedEx);
		
		Exception actualEx = assertThrows(ResponseStatusException.class, () -> getLoansController.getLoans());
		
		assertEquals(mockedEx, actualEx);
	}
	
	@Test
	public void GetLoansTest_raisesInternalServiceExForUnexpectedEx() {
		NullPointerException mockedEx = mock(NullPointerException.class);
		when(user.getRole()).thenReturn(UserRole.USER);
		when(user.getUsername()).thenReturn(TestConstants.USERNAME);
		when(sessionContext.getUser()).thenReturn(user);
		when(getLoansService.getLoanByUser(TestConstants.USERNAME))
				.thenThrow(mockedEx);
		
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, () -> getLoansController.getLoans());
		
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualEx.getStatusCode());
		assertEquals(ErrorMessages.SERVER_ERROR, actualEx.getReason());
	}

	@Test
	public void GetLoansTest_invalidUserRole() {
		UserRole TEST_ROLE = mock(UserRole.class);
        //when(TEST_ROLE.ordinal()).thenReturn(2);
		//mock(UserRole.class);
		when(user.getRole()).thenReturn(TEST_ROLE);
		when(sessionContext.getUser()).thenReturn(user);
		
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, () -> getLoansController.getLoans());
		
		assertEquals(HttpStatus.BAD_REQUEST, actualEx.getStatusCode());
		assertEquals(ErrorMessages.INVAILD_USER, actualEx.getReason());
	}
	
}
