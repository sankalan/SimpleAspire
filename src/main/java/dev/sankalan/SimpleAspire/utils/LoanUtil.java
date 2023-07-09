package dev.sankalan.SimpleAspire.utils;

import java.text.DecimalFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Utility methods
 */
@Component
@Scope("singleton")
public class LoanUtil {
	/**
	 * Application property
	 */
	@Value("${simple-aspire.use-unrounded-amount}")
	boolean shouldNotRoundUpAmount;
	
	/**
	 * Rounds up a double to 2 decimal places based on the app property
	 * @param price
	 * @return rounded up price
	 */
	public double getFormattedPrice(double price) {
		if(shouldNotRoundUpAmount) return price;
		DecimalFormat df=new DecimalFormat("0.00");
		return (double) Double.parseDouble(df.format(price));
	}

	/**
	 * Calculates a date 7 days after the provided date
	 * @param lastDate
	 * @return date that is after a week
	 */
	public Date getNextDate(Date lastDate) {
		final int milliSecondsInOneWeek = 1000 * 60 * 60 * 24 * 7; //each installment is 7 days apart
		return new Date(lastDate.getTime() + milliSecondsInOneWeek);
	}
}
