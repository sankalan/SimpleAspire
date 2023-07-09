package dev.sankalan.SimpleAspire.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.sankalan.SimpleAspire.models.LoanRepaymentSchedule;

@ExtendWith(MockitoExtension.class)
public class LoanRepaymentScheduleConverterTest {
	@InjectMocks
	LoanRepaymentScheduleConverter converter;
	
	List<LoanRepaymentSchedule> repayments;
	String SAMPLE_JSON = "[{\"amount\":5.0,\"date\":61633333800000,\"status\":\"PENDING\",\"outstanding\":5.0},{\"amount\":6.0,\"date\":61633938600000,\"status\":\"PENDING\",\"outstanding\":6.0}]";
	
	@BeforeEach
	public void setup() {
		@SuppressWarnings("deprecation")
		LoanRepaymentSchedule[] schedules = new LoanRepaymentSchedule[] {
				new LoanRepaymentSchedule(5, new Date(2023, 1, 1)),
				new LoanRepaymentSchedule(6, new Date(2023, 1, 8))
		};
		repayments = Arrays.asList(schedules);
		
	}
	
	@Test
	public void convertToDatabaseColumnTest() throws JsonProcessingException {
		assertEquals(SAMPLE_JSON, converter.convertToDatabaseColumn(repayments));
	}
	
	@Test
	public void convertToDatabaseColumnTest_error() throws JsonProcessingException {
		assertEquals(String.valueOf("null"), converter.convertToDatabaseColumn(null));
	}
	
	@Test
	public void convertToEntityAttributeTest() throws JsonProcessingException {
		List<LoanRepaymentSchedule> repays =  converter.convertToEntityAttribute(SAMPLE_JSON);
		assertEquals(repayments.size(), repays.size());
		for(int i=0; i<repays.size(); i++) {
			assertEquals(repayments.get(i).getAmount(), repays.get(i).getAmount());
			assertEquals(repayments.get(i).getDate(), repays.get(i).getDate());
			assertEquals(repayments.get(i).getOutstanding(), repays.get(i).getOutstanding());
			assertEquals(repayments.get(i).getStatus(), repays.get(i).getStatus());
		}
	}
	
	@Test
	public void convertToEntityAttributeTest_error() throws JsonProcessingException {
		assertNull(converter.convertToEntityAttribute("{}"));
	}
}
