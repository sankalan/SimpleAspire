package dev.sankalan.SimpleAspire.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model for Loan data
 */

public class Loan {
	private String id ;
	private String ownerId;
	private double amount;
	private double outstanding;
	private int term;
	private Date date;
	private LoanStatus status;
	private List<LoanRepaymentSchedule> repayments;

	@JsonCreator
	public Loan(
			@JsonProperty("amount") double amount, 
			@JsonProperty("term") int term) {
		this.id = UUID.randomUUID().toString();
		this.amount = amount;
		this.outstanding = amount;
		this.term = term;
		this.date = new Date();
		this.status = LoanStatus.PENDING;
	}

	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the owner
	 */
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @return the outstanding
	 */
	public double getOutstanding() {
		return outstanding;
	}

	/**
	 * @param outstanding the outstanding to set
	 */
	public void setOutstanding(double outstanding) {
		this.outstanding = outstanding;
	}

	/**
	 * @return the term
	 */
	public int getTerm() {
		return term;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @return the status
	 */
	public LoanStatus getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to APPROVED
	 */
	public void approveLoan() {
		this.status = LoanStatus.APPROVED;
	}
	
	/**
	 * @param status the status to PAID
	 */
	public void completeLoanPayment() {
		this.outstanding = 0.0;
		this.status = LoanStatus.PAID;
	}

	/**
	 * @return the repayments
	 */
	public List<LoanRepaymentSchedule> getRepayments() {
		return repayments;
	}


	/**
	 * @param repayments the repayments to set
	 */
	public void setRepayments(List<LoanRepaymentSchedule> repayments) {
		this.repayments = repayments;
	}
	
}
