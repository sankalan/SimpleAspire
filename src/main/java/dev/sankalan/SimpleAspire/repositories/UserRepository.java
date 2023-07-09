package dev.sankalan.SimpleAspire.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.sankalan.SimpleAspire.models.User;


@Repository
public interface UserRepository extends CrudRepository<User, Integer>  {
	User findByUsername(String username);
}

//@Component
//@Scope("singleton")
//public class UserRepository implements Repository<User> {
//
//	private List<User> users = List.of(
//			new User("admin", "admin", UserRole.ADMIN),
//			new User("user1", "user1", UserRole.USER),
//			new User("user2", "user2", UserRole.USER));
//	
//	@Override
//	public void add(User t) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public List<User> getAll() {
//		throw new UnsupportedOperationException();
//	}
//	
//	@Override
//	public User getById(String id) {
//		return users
//				.stream()
//				.filter(user -> id.equals(user.getUserId()))
//				.findFirst()
//				.orElse(null);
//	}
//	
//	public User getUserByName(String username) {
//		return users
//				.stream()
//				.filter(user -> username.equals(user.getUsername()))
//				.findFirst()
//				.orElse(null);
//	}
//
//}
