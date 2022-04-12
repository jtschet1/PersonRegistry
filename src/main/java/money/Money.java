package money;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/* 
 *	slightly modified and INCOMPLETE version of Martin Fowler's Money pattern
 *	from Patterns of Enterprise Application Architecture
 *	v0.1 : initial implementation; 4/12/2015
 */

public class Money {
	private static Logger logger = LogManager.getLogger();

	private static final int [] CENTS = {1, 10, 100, 1000};

	private long amount;
	private Currency currency;
	private Locale locale;

	public Money() {
	}

	public Money(double a, Currency c) {
		currency = c;
		amount = Math.round(a * centFactor());
	}

	public Money(double a, Currency c, Locale l) {
		this(a, c);
		locale = l;
	}

	public Money(long a, Currency c) {
		currency = c;
		amount = a * centFactor();
	}

	/**
	 * Overwrites money instances amount value with new amount.
	 * Assumes Currency and Local have been previously set.
	 * @param amt Amount of money for this object to store
	 */
	public void setAmount(double amt) {
		amount = Math.round(amt * centFactor());
	}
	
	private int centFactor() {
		return CENTS[currency.getDefaultFractionDigits()];
	}

	public BigDecimal amount() {
		return BigDecimal.valueOf(amount, currency.getDefaultFractionDigits());
	}

	public Currency currency() {
		return currency;
	}

	public static Money dollars(double amount) {
		Money m = new Money(amount, Currency.getInstance("USD"));
		m.locale = Locale.US;
		return m;
	}

	public boolean equals(Object o) {
		return (o instanceof Money) && equals((Money) o);
	}

	public boolean equals(Money o) {
		return currency.equals(o.currency) && (amount == o.amount);
	}

	public int hashCode() {
		return (int) (amount ^ (amount >>> 32));
	}

	public Money add(Money o) {
		if(!currency.equals(o.currency)) 
			throw new IllegalArgumentException("Currency types do not match!");
		return newMoney(amount + o.amount);
	}

	private Money newMoney(long a) {
		Money m = new Money();
		m.amount = a;
		m.currency = this.currency;
		m.locale = this.locale;

		return m;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Money subtract(Money o) {
		if(!currency.equals(o.currency)) 
			throw new IllegalArgumentException("Currency types do not match!");
		return newMoney(amount - o.amount);
	}

	/**
	 * Parses string parameter and returns double representation of amount
	 * @param amt formatted string containing the amount to be parsed
	 * @return amount contained in formatted string as a double
	 */
	public static double parseDouble(String amt) {
		//use a regular expression to extract only numeric digits and .
		//NOTE: this USD-specific and should be fixed in the future
		String newAmt = amt.replaceAll("[^\\d.]+", "");
		return Double.parseDouble(newAmt);
	}
	
	/**
	 * Format money amount for output using given currency
	 * Notice that the conversion of long int amount back to a floating point number is JUST for formatting output
	 * Not for performing operations like add or subtract 
	 */
	public String toString() {
		java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(locale);

		return currencyFormatter.format((double) amount / centFactor());
	}
	
	/**
	 * Divide a Money amount into n equal parts. Remainder is in last part so that sum equals original amount.
	 * @param n divisor of money amount
	 * @return array of n money elements precisely totaling original amount 
	 */
	public Money [] divide(int n) {
		//n must be > 0
		if(n < 1)
			throw new IllegalArgumentException("n must be > 0!");
		//create the destination array of money objects
		Money [] arr = new Money[n];
		//set last element to original amount (will get sum of other elements subtracted from it later)
		arr[n - 1] = this.newMoney(this.amount);
		
		//if dividing by 1 then return new instance of this
		if(n == 1) {
			return arr;
		}
		
		//init sum
		Money sum = Money.dollars(0.00);
		//calc division of money
		double div = this.amount().doubleValue() / n;
		
		//iterate n - 1 times, adding amount / n to sum
		for(int i = 0; i < (n - 1); i++) {
			arr[i] = Money.dollars(div);
			sum = sum.add(arr[i]);
		}
		//subtract sum from last element
		arr[n - 1] = arr[n - 1].subtract(sum);
		
		return arr;
	}
}