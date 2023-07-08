package dev.sankalan.SimpleAspire.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.repositories.UserRepository;
import io.micrometer.common.util.StringUtils;

@Component
public class AuthService {
	
	@Autowired
	UserRepository userRepo;
	
	public User fetchUserContext(String basicAuthHeaderValue) {
		if (!StringUtils.isBlank(basicAuthHeaderValue) 
				&& basicAuthHeaderValue.toLowerCase().startsWith("basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = basicAuthHeaderValue.substring("Basic".length()).trim();
			byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded, StandardCharsets.UTF_8);
			// credentials = username:password
			final String[] values = credentials.split(":", 2);

			if (values.length == 2) {
				String username = values[0];
				String password = values[1];
				User user = userRepo.getUserByName(username);
				if ((user != null) && user.authenticate(password)) {
					return user;
				}
			}
		}
		return null;
	}

}
