package dev.sankalan.SimpleAspire.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.repositories.UserRepository;
import io.micrometer.common.util.StringUtils;

/**
 * Service for auth header validation
 */
@Component
public class AuthService {
	
	@Autowired
	UserRepository userRepo;
	
	/**
	 * Fetches the user context based on auth header data
	 * @param basicAuthHeaderValue
	 * @return {@link User}
	 */
	public User fetchUserContext(String basicAuthHeaderValue) {
		if (!StringUtils.isBlank(basicAuthHeaderValue) 
				&& basicAuthHeaderValue.startsWith("Basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = basicAuthHeaderValue.substring("Basic".length()).trim();
			byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded, StandardCharsets.UTF_8);
			// credentials = username:password
			final String[] values = credentials.split(":", 2);

			if (values.length == 2) {
				String username = values[0];
				String password = values[1];
				User user = userRepo.findByUsername(username);
				if ((user != null) && user.authenticate(password)) {
					return user;
				}
			}
		}
		return null;
	}

}
