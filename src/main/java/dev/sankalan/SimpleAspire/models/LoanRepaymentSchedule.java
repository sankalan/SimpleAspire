package dev.sankalan.SimpleAspire.models;

import java.util.Date;

/**
 * Model for Loan repay schedule data
 */
public class LoanRepaymentSchedule {
	private double amount;
	private Date date;
	private LoanRepaymentStatus status;
	private double outstanding;

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}
	
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
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
	public LoanRepaymentStatus getStatus() {
		return status;
	}
	
	/**
	 * Completes payment of a loan
	 */
	public void makePayment() {
		this.status = LoanRepaymentStatus.PAID;
		this.outstanding = 0.0;
	}

	/**
	 * Processes partial payment
	 * @param amount
	 */
	public void adjustDueAmount(double amount) {
		this.outstanding -= amount;
	}

	/**
	 * @return the outstanding
	 */
	public double getOutstanding() {
		return outstanding;
	}

	/**
	 * @param due the outstanding to set
	 */
	public void setOutstanding(double due) {
		this.outstanding = due;
	}
	
	public LoanRepaymentSchedule() {
		
	}

	public LoanRepaymentSchedule(double amount, Date date) {
		this.amount = amount;
		this.date = date;
		this.outstanding = amount;
		this.status = LoanRepaymentStatus.PENDING;
	}
}
