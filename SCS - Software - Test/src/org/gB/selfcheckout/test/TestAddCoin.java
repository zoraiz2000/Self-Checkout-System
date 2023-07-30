package org.gB.selfcheckout.test;

import java.math.BigDecimal;

import org.gB.selfcheckout.software.AddCoin;
import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.State;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

@SuppressWarnings("deprecation")

/**
 * A test suite for org.g30.selfcheckout.AddCoin.
 */
public class TestAddCoin {
	private State state;

	@Before
	public void setupTests() {
		// Initialize State.
		try {
			state = Main.init(100, 1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Set variables to respective components of State.
		assert state != null;
	}

	// Ensure the payment total is being accumulated correctly.
	@Test
	public void testInsertCoin() throws Exception {
		state.addCoin = new AddCoin(state);

		BigDecimal total = new BigDecimal(0.05);
		state.addCoin.validCoinDetected(null, new BigDecimal(0.05));
		Assert.assertEquals(total, state.paymentTotal);
		total = total.add(new BigDecimal(0.1));
		state.addCoin.validCoinDetected(null, new BigDecimal(0.1));
		Assert.assertEquals(total, state.paymentTotal);
		total = total.add(new BigDecimal(0.25));
		state.addCoin.validCoinDetected(null, new BigDecimal(0.25));
		Assert.assertEquals(total, state.paymentTotal);
		total = total.add(new BigDecimal(1.0));
		state.addCoin.validCoinDetected(null, new BigDecimal(1.0));
		Assert.assertEquals(total, state.paymentTotal);
		total = total.add(new BigDecimal(2.0));
		state.addCoin.validCoinDetected(null, new BigDecimal(2.0));
		Assert.assertEquals(total, state.paymentTotal);
	}

	@Test
	public void testEnabled() {
		state.addCoin = new AddCoin(state);
		state.addCoin.enabled(state.scs.coinValidator);
		Assert.assertTrue(true);
	}

	@Test
	public void testInvalidCoinDetected() {
		state.addCoin = new AddCoin(state);
		state.addCoin.invalidCoinDetected(state.scs.coinValidator);
		Assert.assertTrue(true);
	}

	// Ensure the total does not change when the coin slot is disabled.
	@Test
	public void testInsertDisabled() {
		State state = new State();
		state.addCoin = new AddCoin(state);
		state.addCoin.disabled(null);
		state.addCoin.validCoinDetected(null, new BigDecimal(0.1));
		Assert.assertEquals(new BigDecimal(0), state.paymentTotal);
	}
}
