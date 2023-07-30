package org.gB.selfcheckout.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.State;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;

public class TestAddCustomerBags {

	private State state; // Stores a program state for testing.

	private class Bag extends Item {
		protected Bag(double weightInGrams) {
			super(weightInGrams);
		}
	}

	// Initializes a state for testing with a sensible weight limits
	// an an instance of the AddItemToBage use case class.
	@Before
	public void setup() throws Exception {
		state = Main.init(500, 10);
	}

	// Ensures loading the bagging area scale with valid weight
	// runs smoothly.
	@Test
	public void testValidBagLoading() throws OverloadException {
		Item bag = new Bag(50.0);
		state.addBag(bag);
		assertEquals(state.getExpectedBagWeight(), state.scs.baggingArea.getCurrentWeight(), 0.1);
	}

	/*
	 * Ensures that the overload flag is indeed set when the bagging area scale is
	 * overloaded. Also checks if the overload flag is set to false when the bag is
	 * taken off the scale
	 */
	@Test
	public void testOverloadAndOutOfOverloadWithBag() {
		Item bag = new Bag(510.0);
		state.addBag(bag);
		assertTrue(state.customerBags.overloadFlag == true);
		state.removeBag(bag);
		assertFalse(state.customerBags.overloadFlag == true);
		assertEquals(state.getExpectedBagWeight(), 0.0, 0.1);
	}

	// Fails to add a bag when scale is overloaded .
	@Test
	public void addBagWhileScaleOverlaod() {
		Item bag1 = new Bag(510.0);
		Item bag2 = new Bag(50.0);
		state.addBag(bag1);
		assertFalse(state.addBag(bag2));
	}

	// Fails to add a bag while system is in payment mode.
	@Test
	public void testAddingBagDuringPaymentPhase() {
		state.paymentEnabled = true;
		Item bag = new Bag(50.0);
		assertFalse(state.addBag(bag));
	}

	// Fails to add a bag to scale when system is expecting
	// an item to be loaded instead.
	@Test
	public void testAddingBagDuringItemBaggingPhase() {
		state.waitingForBagging = true;
		Item bag = new Bag(50.0);
		assertFalse(state.addBag(bag));
	}

	// Fails to add a bag to scale when the scale is disabled.
	@Test
	public void testAddingBagToDisabledScale() {
		state.scs.baggingArea.disable();
		Item bag = new Bag(50.0);
		assertFalse(state.addBag(bag));
	}
}
