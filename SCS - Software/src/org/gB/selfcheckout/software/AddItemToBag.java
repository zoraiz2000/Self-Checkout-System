package org.gB.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

/**
 * Class to handle the use case where the user inserts a valid banknote into the
 * system.
 */
public class AddItemToBag implements ElectronicScaleObserver {
	private State state; // The state of the self checkout station's software.
	private boolean enabled; // Indicates whether the watched device is enabled.

	/**
	 * Instantiates this use case handler.
	 * 
	 * @param state The instance of the system state which contains the scale that
	 *              will interact with items.
	 */
	public AddItemToBag(State state) {
		this.state = state;
		this.enabled = true;
	}

	/*
	 * Called when the electronic scale slot is enabled.
	 * 
	 * @param device The scale being enabled.
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = true;
	}

	/*
	 * Called when the electronic scale slot is disabled.
	 * 
	 * @param device The scale being disabled.
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = false;
	}

	/**
	 * Handles weight change updates from the bagging area scale (if the scale is
	 * enabled). If the waitingForBagging flag is set in the state and the weight is
	 * as expected, the flag will be un-set and the system's barcode scanners are
	 * enabled. If the flag is not set, then an error will be issued to the
	 * interface if the weight is not as expected.
	 *
	 * @param scale         The scale experiencing a weight change.
	 * @param weightInGrams The weight currently on the indicated scale.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		if (!this.enabled)
			return;
		if (state.waitingForBagging) { // If an item is expected,
			// and the weight is as expected,
			if (state.getExpectedWeight() == weightInGrams) {
				// then re-enable the scanning status.
				state.scs.mainScanner.enable();
				state.scs.handheldScanner.enable();
				state.waitingForBagging = false;
				state.itemScanned = false;
			}

			// Issue an error to the interface for an unprompted weight change.
		} else if (state.getExpectedWeight() != weightInGrams)
			Main.error("Unexpected weight detected.");
		// state.expectedWeight = weightInGrams;
	}

	/**
	 * Notifies the interface of an overload condition (if the scale is enabled).
	 * 
	 * @param scale The scale experiencing an overload.
	 */
	@Override
	public void overload(ElectronicScale scale) {
		if (this.enabled && !state.waitingForBagging)
			Main.error("Scale overloaded.");
	}

	/**
	 * Notifies the interface that a scale is no longer in an overload condition (if
	 * the scale is enabled).
	 * 
	 * @param scale The scale no longer experiencing an overload.
	 */
	@Override
	public void outOfOverload(ElectronicScale scale) {
		if (this.enabled && !state.waitingForBagging)
			Main.error("Scale no longer overloaded.");
	}
}
