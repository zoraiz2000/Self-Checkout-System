package org.gB.selfcheckout.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.gB.selfcheckout.software.AddItemAfterPayment;
import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.State;
import org.junit.Before;
import org.junit.Test;

public class TestAddItemAfterPayment {
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

	@Test
	public void testAddItemAfterPayment() {
		AddItemAfterPayment addItemAfterPayment = new AddItemAfterPayment(state);

		assertNotNull(addItemAfterPayment);
	}

	@Test
	public void testEnabled() {
		AddItemAfterPayment addItemAfterPayment = new AddItemAfterPayment(state);

		addItemAfterPayment.enabled(null);

		assertFalse(state.scs.mainScanner.isDisabled());
	}
}
