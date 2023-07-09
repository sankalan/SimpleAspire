package dev.sankalan.SimpleAspire.utils;

/**
 * Holds all user facing error messages
 * This structure can be used for L10N
 */
public class ErrorMessages {
	public static final String AUTH_FAILED = 
			"Authorization failed! Make sure you are passing auth header with valid username and password.";
	public static final String SERVER_ERROR = 
			"Unexpected error occured at server.";
	public static final String INVAILD_USER = 
			"User is not valid.";
	public static final String NOT_AUTHORISED = 
			"User is not authorised to perform this action.";
	public static final String NOT_ELIGIBLE_FOR_PAYMENT_PENDING = 
			"Can't process payment for unapproved loans.";
	public static final String NOT_ELIGIBLE_FOR_PAYMENT_PAID = 
			"Can't process any new payment as the loan is already paid.";
	public static final String INVALID_REPAYMENT_SCHEDULE_AMOUNT_MISMATCH = 
			"Repayment amount should exactly match with loan amount.";
	public static final String INVALID_REPAYMENT_SCHEDULE_TERMS = 
			"Repayment terms should be 1 week apart.";
	public static final String INVALID_STATUS_TO_APPROVE_LOAN = 
			"Loan cannot be approved as status in not PENDING.";
	public static final String INSUFFICIENT_REPAYMENT = 
			"Loan repayment amount cannot be less than the next repayment amount.";
	public static final String LOAN_NOT_FOUND = 
			"No loan found for this id";
	public static final String USER_NOT_FOUND = 
			"No user found.";
	public static final String NOT_ELIGIBLE_FOR_APPROVAL = 
			"Loan is not in PENDING status.";
	public static final String INVALID_INPUT_FOR_CREATE = 
			"Invalid input, both loan amount and term should be non zero positive numbers.";
};
