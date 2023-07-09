package dev.sankalan.SimpleAspire.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Model for User data
 */
@Entity
@Table(name = "user_table")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private UserRole role;
	
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return id;
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
	
	/**
	 * Validate password
	 * @param pswd
	 * @return boolean indication authentication success
	 */
	public boolean authenticate(String pswd) {
		return pswd.equals(password);
	}
	
}
