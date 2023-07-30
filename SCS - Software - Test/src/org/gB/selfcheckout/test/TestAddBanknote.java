package org.gB.selfcheckout.test;

import java.math.BigDecimal;
import java.util.Currency;

import org.gB.selfcheckout.software.AddBanknote;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Test;

/**
 * A test suite for org.g30.selfcheckout.AddBanknote.
 */
public class TestAddBanknote {
	// Ensure the payment total is being accumulated correctly.
	@Test
	public void testInsertBanknote() throws Exception {
		State state = new State();
		state.addBanknote = new AddBanknote(state);
		state.addBanknote.validBanknoteDetected(null, Currency.getInstance("CAD"), 5);
		Assert.assertEquals(new BigDecimal(5), state.paymentTotal);
		state.addBanknote.validBanknoteDetected(null, Currency.getInstance("CAD"), 10);
		Assert.assertEquals(new BigDecimal(15), state.paymentTotal);
		state.addBanknote.validBanknoteDetected(null, Currency.getInstance("CAD"), 20);
		Assert.assertEquals(new BigDecimal(35), state.paymentTotal);
		state.addBanknote.validBanknoteDetected(null, Currency.getInstance("CAD"), 50);
		Assert.assertEquals(new BigDecimal(85), state.paymentTotal);
	}

	// Ensure the total does not change when the banknote slot is disabled.
	@Test
	public void testInsertDisabled() {
		State state = new State();
		state.addBanknote = new AddBanknote(state);
		state.addBanknote.disabled(null);
		state.addBanknote.validBanknoteDetected(null, Currency.getInstance("CAD"), 5);
		Assert.assertEquals(new BigDecimal(0), state.paymentTotal);
	}
}
