package org.gB.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class AddCustomerBags implements ElectronicScaleObserver {
	public State state; // System state
	public boolean overloadFlag; // Indicates if scale is overload.

	/**
	 * @param state An instance of the state of self checkout system.
	 */
	public AddCustomerBags(State state) {
		this.state = state;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// Ignored.
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// Ignored.
	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		// Ignored.
	}

	/**
	 * Sets the flag indicating scale is overloaded.
	 * 
	 * @param scale The scale that has been overloaded.
	 */
	@Override
	public void overload(ElectronicScale scale) {
		this.overloadFlag = true;
	}

	/**
	 * Sets the flag indicating scale is out of overload.
	 * 
	 * @param scale The scale out of overload.
	 */
	@Override
	public void outOfOverload(ElectronicScale scale) {
		this.overloadFlag = false;
	}
}
