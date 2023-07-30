package org.gB.selfcheckout.test;

import java.math.BigDecimal;
import java.util.Map;

import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.State;
import org.junit.Test;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;

import junit.framework.Assert;

@SuppressWarnings("deprecation")

/**
 * A test suite for org.g30.selfcheckout.Main.
 */
public class TestMain {
	// Ensure an exception is thrown on negative max weight.
	@Test(expected = SimulationException.class)
	public void testNegativeMaxWeight() throws Exception {
		Main.init(-5, 5);
	}

	// Ensure an exception is thrown on negative sensitivity.
	@Test(expected = SimulationException.class)
	public void testNegativeSensitivity() throws Exception {
		Main.init(5, -5);
	}

	// Ensure an exception is thrown on 0 max weight.
	@Test(expected = SimulationException.class)
	public void testZeroMaxWeight() throws Exception {
		Main.init(0, 5);
	}

	// Ensure an exception is thrown on 0 sensitivity.
	@Test(expected = SimulationException.class)
	public void testZeroSensitivity() throws Exception {
		Main.init(5, 0);
	}

	// With valid scale parameters, ensure the state is correctly initialized.
	@Test
	public void testNormalParameters() throws Exception {
		State state = Main.init(2000, 1);
		// Ensure members are initialized.
		Assert.assertNotNull(state.idb);
		Assert.assertNotNull(state.scs);
		Assert.assertNotNull(state.addBanknote);
		Assert.assertNotNull(state.addCoin);
		Assert.assertNotNull(state.addItemToBag);
		// Assert.assertNotNull(state.checkout);
		Assert.assertNotNull(state.paymentMode);
		Assert.assertNotNull(state.scanItem);

		// Ensure all devices are enabled or disabled as required.
		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
		Assert.assertFalse(state.scs.baggingArea.isDisabled());
		Assert.assertTrue(state.scs.scanningArea.isDisabled());
		Assert.assertTrue(state.scs.printer.isDisabled());
		Assert.assertTrue(state.scs.cardReader.isDisabled());
		Assert.assertTrue(state.scs.banknoteInput.isDisabled());
		Assert.assertTrue(state.scs.banknoteOutput.isDisabled());
		Assert.assertTrue(state.scs.banknoteValidator.isDisabled());
		Assert.assertTrue(state.scs.banknoteStorage.isDisabled());
		for (Map.Entry<Integer, BanknoteDispenser> d : state.scs.banknoteDispensers.entrySet())
			Assert.assertTrue(d.getValue().isDisabled());
		Assert.assertTrue(state.scs.coinSlot.isDisabled());
		Assert.assertTrue(state.scs.coinTray.isDisabled());
		Assert.assertTrue(state.scs.coinValidator.isDisabled());
		Assert.assertTrue(state.scs.coinStorage.isDisabled());
		for (Map.Entry<BigDecimal, CoinDispenser> d : state.scs.coinDispensers.entrySet())
			Assert.assertTrue(d.getValue().isDisabled());
	}
}
