/**
 * 
 */
package dev.sankalan.SimpleAspire.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.sankalan.SimpleAspire.models.Loan;

/**
 * 
 */
@Repository
public interface LoanRepository extends CrudRepository<Loan, Integer>  {
	List<Loan> findByOwnerId(int ownerId);
}
