package dev.sankalan.SimpleAspire.converters;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sankalan.SimpleAspire.models.LoanRepaymentSchedule;
import jakarta.persistence.AttributeConverter;

/**
 * Handles to and fro convertion from {@link LoanRepaymentSchedule} to String for DB
 */
public class LoanRepaymentScheduleConverter implements AttributeConverter<List<LoanRepaymentSchedule>, String> {

	private final Logger log = LogManager.getLogger(getClass());
	private final ObjectMapper objectMapper = new ObjectMapper();
	
    @Override
    public String convertToDatabaseColumn(List<LoanRepaymentSchedule> repayments) {

        String customerInfoJson = null;
        try {
            customerInfoJson = objectMapper.writeValueAsString(repayments);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return customerInfoJson;
    }

    @Override
    public List<LoanRepaymentSchedule> convertToEntityAttribute(String repaymentsJson) {

    	List<LoanRepaymentSchedule> repayments = null;
        try {
        	repayments = objectMapper.readValue(repaymentsJson, 
            	new TypeReference<List<LoanRepaymentSchedule>>() {});
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return repayments;
    }
}