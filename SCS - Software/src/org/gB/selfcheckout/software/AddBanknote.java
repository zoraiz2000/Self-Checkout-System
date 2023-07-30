package org.gB.selfcheckout.software;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;

/**
 * Class to handle the use case where the user inserts a valid banknote into the
 * system.
 */
public class AddBanknote implements BanknoteValidatorObserver {
	private State state; // The state of the self checkout station's software.
	private boolean enabled; // Indicates whether the watched device is enabled.

	/**
	 * Instantiates this use case handler.
	 * 
	 * @param state The instance of the system state to which inserted banknote's
	 *              value are to be accumulated.
	 */
	public AddBanknote(State state) {
		this.state = state;
		this.enabled = true;
	}

	/*
	 * Called when the observed banknote slot is enabled.
	 * 
	 * @param device The banknote slot being enabled.
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = true;
	}

	/*
	 * Called when the observed banknote slot is disabled.
	 * 
	 * @param device The banknote slot being disabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = false;
	}

	/**
	 * Called when a valid banknote is inserted into the system (if the banknote
	 * slot is enabled), adding its value to the total in the software's state.
	 * 
	 * @param validator A reference to the the validator instance that called this
	 *                  method.
	 * @param currency  The currency instance for the currency of the inserted
	 *                  banknote.
	 * @param value     The value of the inserted banknote.
	 */
	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		if (this.enabled)
			state.paymentTotal = state.paymentTotal.add(new BigDecimal(value));
	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		// Ignored.
	}

}
