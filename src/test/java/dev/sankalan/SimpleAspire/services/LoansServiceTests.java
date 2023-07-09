package dev.sankalan.SimpleAspire.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import dev.sankalan.SimpleAspire.models.User;
import dev.sankalan.SimpleAspire.repositories.LoanRepository;
import dev.sankalan.SimpleAspire.repositories.UserRepository;
import dev.sankalan.SimpleAspire.testHelpers.TestConstants;
import dev.sankalan.SimpleAspire.utils.ErrorMessages;
import dev.sankalan.SimpleAspire.utils.LoanUtil;

@ExtendWith(MockitoExtension.class)
public class LoansServiceTests {
	@InjectMocks
	LoansService loansService;

	@Mock
	LoanRepository loanRepo;
	@Mock
	UserRepository userRepo;
	@Mock
	LoanUtil loanUtil;
	@Mock
	User user;
	@Mock
	List<Loan> mockedLoans;
	@Mock
	Loan loan1;
	@Mock
	Loan loan2;
	
	@BeforeEach
    public void setUp() {
        
    }
	
	@Test
	public void getAllLoansTest() {
		Loan[] loanArr = new Loan[] {
				loan1,
				loan2
		};
		when(loanRepo.findAll()).thenReturn(Arrays.asList(loanArr));
		
		List<Loan> loans = loansService.getAllLoans();
		
		assertEquals(loan1, loans.get(0));
		assertEquals(loan2, loans.get(1));
	}
	
	@Test
	public void getLoansByUser_happyCase() {
		when(userRepo.findByUsername(TestConstants.USERNAME)).thenReturn(user);
		when(user.getUserId()).thenReturn(TestConstants.USER_ID);
		when(loanRepo.findByOwnerId(TestConstants.USER_ID)).thenReturn(mockedLoans);
		
		assertEquals(mockedLoans, loansService.getLoanByUser(TestConstants.USERNAME));
	}
	
	@Test
	public void getLoansByUser_userNotFound() {
		when(userRepo.findByUsername(TestConstants.USERNAME)).thenReturn(null);
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class,
						() -> loansService.getLoanByUser(TestConstants.USERNAME));
		
		assertEquals(HttpStatus.NOT_FOUND, actualEx.getStatusCode());
		assertEquals(ErrorMessages.USER_NOT_FOUND, actualEx.getReason());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void createLoan_happyCase() {
		when(userRepo.findByUsername(TestConstants.USERNAME)).thenReturn(user);
		when(user.getUserId()).thenReturn(TestConstants.USER_ID);
		double LOAN_AMOUNT = 4000;
		int LOAN_TERM = 3;
		Loan loan = new Loan(LOAN_AMOUNT, LOAN_TERM);
		Date date = new Date();
		Date repaymentDates = new Date();
		when(loanUtil.getNextDate(any(Date.class))).thenReturn(repaymentDates);
		when(loanUtil.getFormattedPrice(any(Double.class))).thenCallRealMethod();
		
		loansService.createLoan(loan, TestConstants.USERNAME);
		assertEquals(TestConstants.USER_ID, loan.getOwnerId());
		assertEquals(LOAN_AMOUNT, loan.getAmount());
		assertEquals(LOAN_TERM, loan.getTerm());
		assertEquals(LOAN_AMOUNT, loan.getOutstanding());
		assertEquals(LoanStatus.PENDING, loan.getStatus());
		assertEquals(date.getDate(), loan.getDate().getDate());
		assertEquals(date.getMonth(), loan.getDate().getMonth());
		assertEquals(date.getYear(), loan.getDate().getYear());
		
		assertEquals(LOAN_TERM, loan.getRepayments().size());
		
		for(LoanRepaymentSchedule repay:loan.getRepayments()) {
			assertEquals(repay.getAmount(), repay.getOutstanding());
			assertEquals(repay.getStatus(), LoanRepaymentStatus.PENDING);
			assertEquals(repaymentDates, repay.getDate());
		}
		
		assertEquals(1333.33, loan.getRepayments().get(0).getAmount());
		assertEquals(1333.33, loan.getRepayments().get(1).getAmount());
		assertEquals(1333.34, loan.getRepayments().get(2).getAmount());
		
		verify(loanRepo, times(1)).save(loan);
	}
	
	@Test
	public void createLoan_invalidInput_Amount() {
		when(loan1.getAmount()).thenReturn(-1.0);
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, 
				() -> loansService.createLoan(loan1, TestConstants.USERNAME));
		assertEquals(HttpStatus.BAD_REQUEST, actualEx.getStatusCode());
		assertEquals(ErrorMessages.INVALID_INPUT_FOR_CREATE, actualEx.getReason());
	}
	
	@Test
	public void createLoan_invalidInput_Term() {
		when(loan1.getAmount()).thenReturn(1.0);
		when(loan1.getTerm()).thenReturn(0);
		ResponseStatusException actualEx = assertThrows(ResponseStatusException.class, 
				() -> loansService.createLoan(loan1, TestConstants.USERNAME));
		assertEquals(HttpStatus.BAD_REQUEST, actualEx.getStatusCode());
		assertEquals(ErrorMessages.INVALID_INPUT_FOR_CREATE, actualEx.getReason());
	}
	
	@Test
	public void createLoan_userNotFound() {
		when(userRepo.findByUsername(TestConstants.USERNAME)).thenReturn(null);
		when(loan1.getAmount()).thenReturn(1.0);
		when(loan1.getTerm()).thenReturn(5);
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class,
						() -> loansService.createLoan(loan1, TestConstants.USERNAME));
		
		assertEquals(HttpStatus.NOT_FOUND, actualEx.getStatusCode());
		assertEquals(ErrorMessages.USER_NOT_FOUND, actualEx.getReason());
	}
	
	@Test
	public void approveLoan_happyCase() {
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.of(loan1));
		when(loan1.getStatus()).thenReturn(LoanStatus.PENDING);
		loansService.approveLoan(TestConstants.LOAN_ID);
		
		verify(loan1, times(1)).approveLoan();
		verify(loanRepo, times(1)).save(loan1);
	}
	
	@Test
	public void approveLoan_loanNotFound() {
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.empty());
		
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class,
						() -> loansService.approveLoan(TestConstants.LOAN_ID));
		
		assertEquals(HttpStatus.NOT_FOUND, actualEx.getStatusCode());
		assertEquals(ErrorMessages.LOAN_NOT_FOUND, actualEx.getReason());
	}
	
	@Test
	public void approveLoan_loanNotPending() {
		when(loanRepo.findById(TestConstants.LOAN_ID)).thenReturn(Optional.of(loan1));
		when(loan1.getStatus()).thenReturn(LoanStatus.APPROVED);
		ResponseStatusException actualEx = 
				assertThrows(ResponseStatusException.class,
						() -> loansService.approveLoan(TestConstants.LOAN_ID));
		
		assertEquals(HttpStatus.CONFLICT, actualEx.getStatusCode());
		assertEquals(ErrorMessages.NOT_ELIGIBLE_FOR_APPROVAL, actualEx.getReason());
	}
}
