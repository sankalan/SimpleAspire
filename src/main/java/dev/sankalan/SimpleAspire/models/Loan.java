package dev.sankalan.SimpleAspire.models;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.sankalan.SimpleAspire.converters.LoanRepaymentScheduleConverter;

/**
 * Model for Loan data
 */

@Entity
public class Loan {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id ;
	private int ownerId;
	private double amount;
	private double outstanding;
	private int term;
	private Date date;
	private LoanStatus status;
	
	@Lob
	@Convert(converter = LoanRepaymentScheduleConverter.class)
	private List<LoanRepaymentSchedule> repayments;
	
	public Loan() {
		
	}

	@JsonCreator
	public Loan(
			@JsonProperty("amount") double amount, 
			@JsonProperty("term") int term) {
		this.amount = amount;
		this.outstanding = amount;
		this.term = term;
		this.date = new Date();
		this.status = LoanStatus.PENDING;
	}

	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the owner
	 */
	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
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
