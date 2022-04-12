package money;

import java.util.Currency;
import java.util.Locale;

public class DoThis {

	public static void main(String[] args) {
		Money val = new Money(0, Currency.getInstance(Locale.US));

		Money amountToAdd = new Money(0.1, Currency.getInstance(Locale.US));

		//add 10 dimes (0.10) to val
		for(int i = 0; i < 10; i++) {
			val = val.add(amountToAdd);
			System.out.println("val so far " + val.amount());
		}

		//val should equal 1.00
		System.out.println("val is " + val.amount() + " (should be 1.00)");

	}

}
