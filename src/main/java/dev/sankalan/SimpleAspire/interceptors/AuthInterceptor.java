package dev.sankalan.SimpleAspire.interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.services.AuthService;
import dev.sankalan.SimpleAspire.session.SessionContext;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final String AUTH_HEADER_PARAMETER_AUTHERIZATION = "authorization";

	@Autowired
	AuthService authService;
	@Autowired
	SessionContext sessionContext;

	private final Logger log = LogManager.getLogger(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		log.info("Validating request: " + request);

		User user = null;
		try {

			// Grab basic header value from request header object.
			String basicAuthHeaderValue = request.getHeader(AUTH_HEADER_PARAMETER_AUTHERIZATION);

			// Process basic authentication
			user = authService.fetchUserContext(basicAuthHeaderValue);
			
		} catch (Exception e) {
			log.error("Error occured while authenticating request : " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
		}
		
		// If this is invalid request, then set the status as UNAUTHORIZED.
		if (user == null) {
			log.error("Unauthorised access detected.");
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessages.AUTH_FAILED);
		}
		
		sessionContext.setUser(user);

		return true;
	}

}
