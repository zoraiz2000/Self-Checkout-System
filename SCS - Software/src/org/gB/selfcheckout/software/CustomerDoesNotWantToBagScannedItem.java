package org.gB.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class CustomerDoesNotWantToBagScannedItem implements ElectronicScaleObserver {
	private State state; // The state of the self checkout station's software.
	private boolean enabled; // Indicates whether the watched device is enabled.

	public CustomerDoesNotWantToBagScannedItem(State state) {
		this.state = state;
		this.enabled = true;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = true;
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		this.enabled = false;

	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		if (!this.enabled)
			return;

		if (state.getExpectedWeight() == weightInGrams && !state.waitingForBagging) {
			// then re-enable the scanning status.
			state.scs.mainScanner.enable();
			state.scs.handheldScanner.enable();
		}

		// Issue an error to the interface for an unprompted weight change.
		else if (state.getExpectedWeight() != weightInGrams)
			Main.error("Unexpected weight detected.");
		else
			Main.error("Station should not be expecting the item to be bagged");
	}

	@Override
	public void overload(ElectronicScale scale) {
		if (this.enabled)
			Main.error("Scale overloaded.");
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		if (this.enabled)
			Main.error("Scale no longer overloaded.");
	}

}
