/**
 * 
 */
package dev.sankalan.SimpleAspire.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import dev.sankalan.SimpleAspire.models.Loan;

/**
 * 
 */
@Component
@Scope("singleton")
public class LoanRepository implements Repository<Loan>{
	
	private ArrayList<Loan> allLoans;
	
	public LoanRepository() {
		allLoans = new ArrayList<Loan>();
	}

	@Override
	public void add(Loan loan) {
		allLoans.add(loan);
	}

	@Override
	public List<Loan> getAll() {
		return allLoans;
	}

	public List<Loan> getByUserId(String userId) {
		return allLoans
				.stream()
				.filter(loan -> loan.getOwnerId().equals(userId))
				.collect(Collectors.toList());
	}
	
	@Override
	public Loan getById(String id) {
		return allLoans
				.stream()
				.filter(loan -> loan.getId().equals(id))
				.findFirst()
				.orElse(null);
	}
	
}
