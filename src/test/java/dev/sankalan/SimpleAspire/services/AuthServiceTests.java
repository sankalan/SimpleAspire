/**
 * 
 */
package dev.sankalan.SimpleAspire.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.repositories.UserRepository;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
	@InjectMocks
	AuthService authService;
	
	@Mock
	private UserRepository userRepo;
	@Mock
	private User user;
	
	@Test
	public void fetchUserContext_success() {
		when(userRepo.findByUsername(TestConstants.USERNAME))
			.thenReturn(user);
		when(user.authenticate(TestConstants.PASSWORD))
			.thenReturn(true);
		
		User actualUser = authService.fetchUserContext("Basic" + 
						Base64.getEncoder()
								.encodeToString((TestConstants.USERNAME + ":" + TestConstants.PASSWORD).getBytes()));
		assertEquals(user, actualUser);
	}
	
	@Test
	public void fetchUserContext_userNotFound() {
		when(userRepo.findByUsername(TestConstants.USERNAME))
			.thenReturn(null);
		
		User actualUser = authService.fetchUserContext("Basic" + 
						Base64.getEncoder()
								.encodeToString((TestConstants.USERNAME + ":" + TestConstants.PASSWORD).getBytes()));
		assertNull(actualUser);
	}
	
	@Test
	public void fetchUserContext_invalidPassword() {
		when(userRepo.findByUsername(TestConstants.USERNAME))
			.thenReturn(user);
		when(user.authenticate(TestConstants.PASSWORD))
			.thenReturn(false);
		
		User actualUser = authService.fetchUserContext("Basic" + 
						Base64.getEncoder()
								.encodeToString((TestConstants.USERNAME + ":" + TestConstants.PASSWORD).getBytes()));
		assertNull(actualUser);
	}
	
	
	@ParameterizedTest
	@MethodSource("provideAllInvalidHeaders")
	public void fetchUserContext_invalidHeader(String header) {
		assertNull(authService.fetchUserContext(header));
	}
	
	private static Stream<Arguments> provideAllInvalidHeaders() {
	    return Stream.of(
	      Arguments.of(""),
	      Arguments.of(" "),
	      Arguments.of("Not-Starts-With-Basic"),
	      Arguments.of(Base64.getEncoder()
					.encodeToString((TestConstants.USERNAME + ":" + TestConstants.PASSWORD + ":" + "SOMETHING").getBytes()))
	    );
	}

}
