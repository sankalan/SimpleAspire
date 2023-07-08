package dev.sankalan.SimpleAspire.repositories;

import java.util.List;

/**
 * 
 */
public interface Repository<T> {
	void add(T t);
	List<T> getAll();
	T getById(String id);
}
