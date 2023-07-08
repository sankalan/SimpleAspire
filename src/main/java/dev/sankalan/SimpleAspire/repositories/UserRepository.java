package dev.sankalan.SimpleAspire.repositories;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.models.UserRole;

@Component
@Scope("singleton")
public class UserRepository implements Repository<User> {

	private List<User> users = List.of(
			new User("admin", "admin", UserRole.ADMIN),
			new User("user1", "user1", UserRole.USER),
			new User("user2", "user2", UserRole.USER));
	
	@Override
	public void add(User t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> getAll() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public User getById(String id) {
		return users
				.stream()
				.filter(user -> id.equals(user.getUserId()))
				.findFirst()
				.orElse(null);
	}
	
	public User getUserByName(String username) {
		return users
				.stream()
				.filter(user -> username.equals(user.getUsername()))
				.findFirst()
				.orElse(null);
	}

}
