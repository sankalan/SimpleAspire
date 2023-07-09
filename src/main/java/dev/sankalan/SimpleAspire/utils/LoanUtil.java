package dev.sankalan.SimpleAspire.utils;

import java.text.DecimalFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class LoanUtil {
	@Value("${simple-aspire.use-unrounded-amount}")
	boolean shouldNotRoundUpAmount;
	
	public double getFormattedPrice(double price) {
		if(shouldNotRoundUpAmount) return price;
		DecimalFormat df=new DecimalFormat("0.00");
		return (double) Double.parseDouble(df.format(price));
	}

	public Date getNextDate(Date lastDate) {
		final int milliSecondsInOneWeek = 1000 * 60 * 60 * 24 * 7; //each installment is 7 days apart
		return new Date(lastDate.getTime() + milliSecondsInOneWeek);
	}
}
