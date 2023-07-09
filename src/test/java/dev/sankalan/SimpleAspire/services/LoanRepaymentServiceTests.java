package dev.sankalan.SimpleAspire.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.sankalan.SimpleAspire.models.Loan;
import dev.sankalan.SimpleAspire.models.LoanRepaymentSchedule;
import dev.sankalan.SimpleAspire.models.LoanRepaymentStatus;
import dev.sankalan.SimpleAspire.models.LoanStatus;
import dev.sankalan.SimpleAspire.models.Repayment;
import dev.sankalan.SimpleAspire.repositories.LoanRepository;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import dev.sankalan.SimpleAspire.utils.LoanUtil;

@ExtendWith(MockitoExtension.class)
public class LoanRepaymentServiceTests {

	@InjectMocks
	LoanRepaymentService service;
	
	@Mock
	LoanRepository loanRepo;
	@Mock
	LoanUtil loanUtil;
	@Mock
	Loan loan;
	@Mock
	Repayment installment;
	
	
	@Test
	public void addRepayment_happycase() {
		Loan loan1= new Loan(1800.0, 3);
		List<LoanRepaymentSchedule> repayments = Arrays.asList(
				new LoanRepaymentSchedule[] {
						new LoanRepaymentSchedule(600.0, new Date()),
						new LoanRepaymentSchedule(600.0, new Date()),
						new LoanRepaymentSchedule(600.0, new Date())
				}
			);
		loan1.setRepayments(repayments);
		loan1.approveLoan();
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.of(loan1));
		when(installment.getAmount()).thenReturn(1300.0);
		when(loanUtil.getFormattedPrice(any(Double.class))).thenCallRealMethod();
		
		service.addRepayment(TestConstants.LOAN_ID, installment);
		
		assertEquals(LoanStatus.APPROVED, loan1.getStatus());
		
		assertEquals(0.0, repayments.get(0).getOutstanding());
		assertEquals(LoanRepaymentStatus.PAID, repayments.get(0).getStatus());
		assertEquals(0.0, repayments.get(1).getOutstanding());
		assertEquals(LoanRepaymentStatus.PAID, repayments.get(1).getStatus());
		assertEquals(500.0, repayments.get(2).getOutstanding());
		assertEquals(LoanRepaymentStatus.PENDING, repayments.get(2).getStatus());
		
		when(installment.getAmount()).thenReturn(500.0);
		
		service.addRepayment(TestConstants.LOAN_ID, installment);
		
		assertEquals(LoanStatus.PAID, loan1.getStatus());
		
		assertEquals(0.0, repayments.get(0).getOutstanding());
		assertEquals(LoanRepaymentStatus.PAID, repayments.get(0).getStatus());
		assertEquals(0.0, repayments.get(1).getOutstanding());
		assertEquals(LoanRepaymentStatus.PAID, repayments.get(1).getStatus());
		assertEquals(0.0, repayments.get(2).getOutstanding());
		assertEquals(LoanRepaymentStatus.PAID, repayments.get(2).getStatus());
		
	}
	
	@Test
	public void addRepayment_installmentAmountLess() {
		Loan loan1= new Loan(1800.0, 3);
		List<LoanRepaymentSchedule> repayments = Arrays.asList(
				new LoanRepaymentSchedule[] {
						new LoanRepaymentSchedule(600.0, new Date()),
						new LoanRepaymentSchedule(600.0, new Date()),
						new LoanRepaymentSchedule(600.0, new Date())
				}
			);
		loan1.setRepayments(repayments);
		loan1.approveLoan();
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.of(loan1));
		when(installment.getAmount()).thenReturn(500.0);
		
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, 
				() -> service.addRepayment(TestConstants.LOAN_ID, installment));
		assertEquals(HttpStatus.BAD_REQUEST, actualEx.getStatusCode());
		assertEquals(ErrorMessages.INSUFFICIENT_REPAYMENT, actualEx.getReason());
		
		
		
	}
	
	@Test
	public void addRepayment_loanNotFound() {
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.empty());
		
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, 
				() -> service.addRepayment(TestConstants.LOAN_ID, installment));
		assertEquals(HttpStatus.NOT_FOUND, actualEx.getStatusCode());
		assertEquals(ErrorMessages.LOAN_NOT_FOUND, actualEx.getReason());
		
	}
	
	@Test
	public void addRepayment_loanPending() {
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.of(loan));
		when(loan.getStatus()).thenReturn(LoanStatus.PENDING);
		
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, 
				() -> service.addRepayment(TestConstants.LOAN_ID, installment));
		assertEquals(HttpStatus.NOT_ACCEPTABLE, actualEx.getStatusCode());
		assertEquals(ErrorMessages.NOT_ELIGIBLE_FOR_PAYMENT_PENDING, actualEx.getReason());
		
	}
	
	@Test
	public void addRepayment_loanPaid() {
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.of(loan));
		when(loan.getStatus()).thenReturn(LoanStatus.PAID);
		
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, 
				() -> service.addRepayment(TestConstants.LOAN_ID, installment));
		assertEquals(HttpStatus.CONFLICT, actualEx.getStatusCode());
		assertEquals(ErrorMessages.NOT_ELIGIBLE_FOR_PAYMENT_PAID, actualEx.getReason());
		
	}
	
}
