package org.gB.selfcheckout.software;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

public class ScanMembershipCard implements CardReaderObserver {
	private State state;
	private Boolean enable;

	/**
	 * Constructor Method
	 * 
	 * @param state The instance of the system state to which the card will scan
	 */
	public ScanMembershipCard(State state) {
		this.state = state;
	}

	/**
	 * Enable function
	 * 
	 * Enables the card reader
	 * 
	 */
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		enable = true;
	}

	/**
	 * Disable function
	 * 
	 * Disables the card reader
	 * 
	 */
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		enable = false;
	}

	@Override
	public void cardInserted(CardReader reader) {
		// do nothing
	}

	@Override
	public void cardRemoved(CardReader reader) {
		// do nothing
	}

	@Override
	public void cardTapped(CardReader reader) {
		// do nothing
	}

	@Override
	public void cardSwiped(CardReader reader) {
		// do nothing
	}

	/**
	 * Enable function
	 * 
	 * Enables the card reader
	 * 
	 * @param reader The reader that called the function
	 * @param data   The data for the card that is read
	 */
	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		if (enable) {
			this.state.memberNumber = data.getNumber();
		}
	}

}
