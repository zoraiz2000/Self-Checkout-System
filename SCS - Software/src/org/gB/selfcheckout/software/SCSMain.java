package org.gB.selfcheckout.software;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

/**
 * A main class to coordinate the activities of the self checkout system
 * software.
 */
public class SCSMain {

	private SelfCheckoutStation scs;

	/**
	 * Constructs an instance of the self checkout system software state.
	 *
	 * @param scaleMaxWeight   The maximum weight that the self checkout station's
	 *                         electronic scale can output (in grams).
	 * @param scaleSensitivity The sensitivity of the self checkout station's
	 *                         electronic scale (in grams).
	 * @throws Exception Thrown if the SelfCheckoutStation instance cannot be
	 *                   obtained.
	 * @return An instance of the self checkout station software.
	 */
	public State init(int scaleMaxWeight, int scaleSensitivity) throws Exception {

		// The accepted Canadian banknote denominations.
		final int[] billDenoms = { 5, 10, 20, 50 };
		// The accepted Canadian coin denominations.
		final BigDecimal[] coinDenoms = { new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25),
				new BigDecimal(1.0), new BigDecimal(2.0) };

		// Obtain a simulated self checkout station.
		scs = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenoms, coinDenoms, scaleMaxWeight,
				scaleSensitivity);

		// Set the default currency of the coin.
		Coin.DEFAULT_CURRENCY = Currency.getInstance("CAD");
		State state = new State();
		state.idb = new ItemDatabase(); // Instantiate the item database.
		state.scs = scs; // Add the checkout station to the state.

		// Create the AddBanknote handler and attach to a device.
		state.addBanknote = new AddBanknote(state);
		scs.banknoteValidator.attach(state.addBanknote);
		// Create the AddCoin handler and attach to a device.
		state.addCoin = new AddCoin(state);
		scs.coinValidator.attach(state.addCoin);
		// Create the AddItemToBag handler and attach to a device.
		state.addItemToBag = new AddItemToBag(state);
		scs.baggingArea.attach(state.addItemToBag);
		// Create the UseCustomerBags handler and attach to a device.
		state.customerBags = new AddCustomerBags(state);
		scs.baggingArea.attach(state.customerBags);
		// Create the PaymentMode handler.
		state.paymentMode = new PaymentMode(state);
		// Create the ScanItem handler and attach to a device.
		state.scanItem = new ScanItem(state);
		scs.mainScanner.attach(state.scanItem);
		scs.handheldScanner.attach(state.scanItem);
		// Create the ReturnChange handler and attach it to the banknote output slot
		state.returnChange = new ReturnChange(state);
		scs.banknoteOutput.attach(state.returnChange);
		// Instantiate the PrintReceipt observer and attach it to the ReceiptPrinter
		// device
		state.printReceipt = new PrintReceipt(state);
		scs.printer.attach(state.printReceipt);

		state.scs.scanningArea.disable();
		state.scs.baggingArea.disable();
		state.enableScanning();

		return state;
	}

	/**
	 * Stub error method that will be used to announce errors to the user when a UI
	 * becomes available.
	 *
	 * @param type The type of error that took place.
	 */
	public static void error(String type) {
		System.out.println(type);
	}

	/**
	 * Prints a receipt to the customer
	 * 
	 * @throws OverloadException
	 * @throws EmptyException
	 *
	 */
	public void printReceipt(double totalForPrinting) throws EmptyException, OverloadException {

		String printString = new String("Receipt: \n");
		printString = printString.concat("Total: " + totalForPrinting);

		for (int i = 0; i < printString.length(); i++) {
			scs.printer.print(printString.charAt(i));
		}

		scs.printer.cutPaper();
		scs.printer.removeReceipt();
	}

}
