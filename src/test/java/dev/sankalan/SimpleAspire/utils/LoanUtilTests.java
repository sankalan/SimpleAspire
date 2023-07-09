package dev.sankalan.SimpleAspire.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoanUtilTests {
	@InjectMocks
	LoanUtil loanUtil;
	
	@Test
	public void getFormattedPriceTest() {
		assertEquals(13.33, loanUtil.getFormattedPrice(13.3333));
	}
	
	@Test
	public void getFormattedPriceTest_noConversion() {
		loanUtil.shouldNotRoundUpAmount = true;
		assertEquals(13.3333, loanUtil.getFormattedPrice(13.3333));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getNextDateTest() {
		assertEquals(new Date(2023, 1, 8), loanUtil.getNextDate(new Date(2023, 1, 1)));
	}
}
