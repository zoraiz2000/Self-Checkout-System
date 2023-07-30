package org.gB.selfcheckout.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.PaymentMode;
import org.gB.selfcheckout.software.State;
import org.junit.Before;
import org.junit.Test;

public class TestPaymentMode {
	private State state;

	@Before
	public void setup() {
		// Initialize State.
		try {
			state = Main.init(100, 1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Set variables to respective components of State.
		assert state != null;
	}

	/**
	 * Test the constructor of PaymentMode.
	 */
	@Test
	public void testPaymentMode() {
		state.paymentMode = new PaymentMode(state);

		assertNotNull(state.paymentMode);
	}

	/**
	 * Test method for
	 * {@link org.gB.selfcheckout.software.PaymentMode#enablePaymentMode()}.
	 * 
	 * This test just attempts to enable the payment mode.
	 */
	@Test
	public void testEnablePaymentMode() {
		state.paymentMode = new PaymentMode(state);

		state.paymentMode.enablePaymentMode();

		assertTrue(state.paymentMode.isPaymentModeActive());
	}

	/**
	 * Test method for
	 * {@link org.gB.selfcheckout.software.PaymentMode#enablePaymentMode()}.
	 * 
	 * This test attempts to enable the payment mode, but the payment mode is
	 * already active.
	 */
	@Test
	public void testEnablePaymentMode2() {
		state.paymentMode = new PaymentMode(state);

		state.paymentMode.enablePaymentMode();
		state.paymentMode.enablePaymentMode();

		assertTrue(state.paymentMode.isPaymentModeActive());
	}

	/**
	 * Test method for
	 * {@link org.gB.selfcheckout.software.PaymentMode#disablePaymentMode()}.
	 */
	@Test
	public void testDisablePaymentMode() {
		state.paymentMode = new PaymentMode(state);

		state.paymentMode.enablePaymentMode();
		state.paymentMode.disablePaymentMode();

		assertFalse(state.paymentMode.isPaymentModeActive());
	}

	/**
	 * Test method for
	 * {@link org.gB.selfcheckout.software.PaymentMode#disablePaymentMode()}.
	 * 
	 * This test attempts to disable the payment mode, but the payment mode is
	 * already inactive.
	 */
	@Test
	public void testDisablePaymentMode2() {
		state.paymentMode = new PaymentMode(state);

		state.paymentMode.disablePaymentMode();
		state.paymentMode.disablePaymentMode();

		assertFalse(state.paymentMode.isPaymentModeActive());
	}
}
