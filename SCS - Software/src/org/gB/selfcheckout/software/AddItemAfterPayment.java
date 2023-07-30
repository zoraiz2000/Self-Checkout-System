package org.gB.selfcheckout.software;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;

public class AddItemAfterPayment {
	public State state;

	public AddItemAfterPayment(State state) {
		this.state = state;
	}

	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		state.enableScanning();
	}

}
