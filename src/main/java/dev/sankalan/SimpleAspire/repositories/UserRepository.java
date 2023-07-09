package dev.sankalan.SimpleAspire.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.sankalan.SimpleAspire.models.User;

/**
 * Supports operations on User table
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer>  {
	User findByUsername(String username);
}