package dev.sankalan.SimpleAspire.models;

import java.util.UUID;

public class User {
	private String userId;
	private String username;
	private String password;
	private UserRole role;
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @return the role
	 */
	public UserRole getRole() {
		return role;
	}

	public User(String uname, String pswd, UserRole role) {
		this.userId =  UUID.randomUUID().toString();
		this.username = uname;
		this.password = pswd;
		this.role = role;
	}
	
	public boolean authenticate(String pswd) {
		return pswd.equals(password);
	}
	
}
