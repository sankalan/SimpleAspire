package dev.sankalan.SimpleAspire.interceptors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.sankalan.SimpleAspire.models.SessionContext;
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.services.AuthService;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AuthInterceptorTests {
	@InjectMocks
	AuthInterceptor authInterceptor;
	
	@Mock
	AuthService authService;
	@Mock
	SessionContext sessionContext;
	@Mock
	User user;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	PrintWriter printWriter;
	
	private static final String AUTH_HEADER_PARAMETER_AUTHERIZATION = "authorization";
	@Test
	public void preHandle_succuss() throws Exception {
		when(request.getHeader(AUTH_HEADER_PARAMETER_AUTHERIZATION)).thenReturn(TestConstants.AUTH_HEADER);
		when(authService.fetchUserContext(TestConstants.AUTH_HEADER)).thenReturn(user);
		
		assertTrue(authInterceptor.preHandle(request, response, null));
		verify(sessionContext, times(1)).setUser(user);
	}
	
	@Test
	public void preHandle_invalidCred() throws Exception {
		when(request.getHeader(AUTH_HEADER_PARAMETER_AUTHERIZATION)).thenReturn(TestConstants.AUTH_HEADER);
		when(authService.fetchUserContext(TestConstants.AUTH_HEADER)).thenReturn(null);
		when(response.getWriter()).thenReturn(printWriter);
		
		assertFalse(authInterceptor.preHandle(request, response, null));
		verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
		verify(printWriter, times(1)).write(ErrorMessages.AUTH_FAILED);
	}
	
	@Test
	public void preHandle_handlesException() throws Exception {
		when(request.getHeader(AUTH_HEADER_PARAMETER_AUTHERIZATION)).thenReturn(TestConstants.AUTH_HEADER);
		when(authService.fetchUserContext(TestConstants.AUTH_HEADER)).thenThrow(NullPointerException.class);
		when(response.getWriter()).thenReturn(printWriter);
		
		assertFalse(authInterceptor.preHandle(request, response, null));
		verify(response, times(1)).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		verify(printWriter, times(1)).write(ErrorMessages.SERVER_ERROR);
	}

}
