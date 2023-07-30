// ==========================================================
// AddCoin.java
// Authors: Michaud, Matthew; Bin Nakhi, Ayoub; Chen, Jhy-An;
//     Mograbee, Khaled; Wong, Isaac; Dinh, Thuy
// Term Group Project
// SENG 300, Winter 2022
// ==========================================================

package org.gB.selfcheckout.software;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

/**
 * Class to handle the use case where the user inserts a valid coin into the
 * system.
 */
public class AddCoin implements CoinValidatorObserver {
	private State state; // The state of the self checkout station's software.
	private boolean enabled; // Indicates whether the watched device is enabled.

	/**
	 * Instantiates this use case handler.
	 * 
	 * @param state The instance of the system state to which inserted coin's value
	 *              are to be accumulated.
	 */
	public AddCoin(State state) {
		this.state = state;
		this.enabled = true;
	}

	/*
	 * Called when the observed coin slot is enabled.
	 * 
	 * @param device The coin slot being enabled.
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = true;
	}

	/*
	 * Called when the observed coin slot is disabled.
	 * 
	 * @param device The coin slot being disabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = false;
	}

	/**
	 * Called when a valid coin is inserted into the system (if the coin slot is
	 * enabled), adding its value to the total in the software's state.
	 * 
	 * @param validator A reference to the the validator instance that called this
	 *                  method.
	 * @param currency  The currency instance for the currency of the inserted coin.
	 * @param value     The value of the inserted coin.
	 */
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		if (this.enabled)
			this.state.paymentTotal = this.state.paymentTotal.add(value);
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		// Ignored.
	}
}
