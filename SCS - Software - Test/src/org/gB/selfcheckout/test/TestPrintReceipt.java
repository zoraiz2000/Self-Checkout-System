package org.gB.selfcheckout.test;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.gB.selfcheckout.software.AttendantControl;
import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.PrintReceipt;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class TestPrintReceipt {
	private State state; // Stores a program state for testing.
	private Numeral[] numeral1;
	private Numeral[] numeral2;
	private Barcode barcode1;
	private Barcode barcode2;
	private BarcodedProduct appleProduct;
	private BarcodedProduct watermelonProduct;
	private PriceLookupCode pluCode;
	private PLUCodedProduct pluProduct;
	private PrintReceipt printReceipt;
	private AttendantControl attendant;
	private ArrayList<State> scsList;

	@Before
	public void setup() {
		// Initialize State.
		try {
			state = Main.init(10000, 10);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		assert state != null;

		printReceipt = this.state.printReceipt;

		// PLU Coded product
		pluCode = new PriceLookupCode("12345");
		pluProduct = new PLUCodedProduct(pluCode, "Orange", BigDecimal.valueOf(3));

		// "Apple" product, barcode = 10, price = 2, weight = 100.0
		numeral1 = new Numeral[2];
		numeral1[0] = Numeral.one;
		numeral1[1] = Numeral.zero; // 10
		barcode1 = new Barcode(numeral1); // barcode is 10
		appleProduct = new BarcodedProduct(barcode1, "Apple", BigDecimal.valueOf(2), 100.0);

		// "Watermelon" product, barcode = 11, price = 10, weight = 7000.0
		numeral2 = new Numeral[2];
		numeral2[0] = Numeral.one;
		numeral2[1] = Numeral.one; // 11
		barcode2 = new Barcode(numeral2);
		watermelonProduct = new BarcodedProduct(barcode2, "Watermelon", BigDecimal.valueOf(10), 7000.0);

		scsList = new ArrayList<>();
		scsList.add(state);
		attendant = new AttendantControl(scsList);
	}

	@Test
	public void testPrintReceipt() {
		try {
			attendant.addInkCartridge(state, ReceiptPrinter.MAXIMUM_INK);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much ink.");
		}

		try {
			attendant.addPaper(state, ReceiptPrinter.MAXIMUM_PAPER);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much paper.");
		}

		state.addProduct(appleProduct);
		state.addProduct(appleProduct);
		state.addProduct(pluProduct);
		state.addProduct(pluProduct);
		state.addProduct(watermelonProduct);
		state.addProduct(pluProduct);

		state.enablePayment();
		state.paymentTotal = BigDecimal.valueOf(50);

		printReceipt.printReceipt();

		String receipt = state.scs.printer.removeReceipt();
		System.out.println(receipt);
	}

	@Test
	public void testLowOnPaper() {
		try {
			attendant.addInkCartridge(state, ReceiptPrinter.MAXIMUM_INK);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much ink.");
		}

		try {
			attendant.addPaper(state, 10);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much paper.");
		}

		Assert.assertFalse(state.outOfInk);
		Assert.assertFalse(state.outOfPaper);
		Assert.assertFalse(state.lowOnInk);

		Assert.assertTrue(state.lowOnPaper);
	}

	@Test
	public void testLowOnInk() {
		try {
			attendant.addInkCartridge(state, 10);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much ink.");
		}

		try {
			attendant.addPaper(state, ReceiptPrinter.MAXIMUM_PAPER);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much paper.");
		}

		Assert.assertFalse(state.outOfInk);
		Assert.assertFalse(state.outOfPaper);
		Assert.assertFalse(state.lowOnPaper);
		Assert.assertTrue(state.lowOnInk);

	}

	@Test
	public void testOutOfInkAndPaper() {
		try {
			attendant.addInkCartridge(state, 1);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much ink.");
		}

		try {
			attendant.addPaper(state, 1);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much paper.");
		}

		try {
			state.scs.printer.print('c');
		} catch (EmptyException e) {
			Main.error("Printer is out of ink and/or paper.");
		} catch (OverloadException e) {
			Main.error("Reached end of line on paper.");
		}

		Assert.assertTrue(state.outOfInk);
		Assert.assertTrue(state.lowOnInk);

		try {
			state.scs.printer.print('\n');
		} catch (EmptyException e) {
			Main.error("Printer is out of ink and/or paper.");
		} catch (OverloadException e) {
			Main.error("Reached end of line on paper.");
		}
		Assert.assertTrue(state.outOfPaper);
		Assert.assertTrue(state.lowOnPaper);
	}

	@Test
	public void testObserverStatus() {
		state.scs.printer.disable();
		state.scs.printer.enable();
	}

	// These setters are only used for correcting software estimates of ink/paper
	// amount
	@Test
	public void testPaperInkSetters() {
		try {
			attendant.addInkCartridge(state, 1);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much ink.");
		}

		try {
			attendant.addPaper(state, 1);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much paper.");
		}

		state.printReceipt.setInkRemaining(10);
		state.printReceipt.setPaperRemaining(10);
		Assert.assertEquals(10, state.charactersOfInkRemaining);
		Assert.assertEquals(10, state.linesOfPaperRemaining);
	}

	@Test
	public void testEmptyError() {
		try {
			attendant.addInkCartridge(state, 0);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much ink.");
		}

		try {
			attendant.addPaper(state, 1);
		} catch (OverloadException e) {
			Main.error("Overload: Added too much paper.");
		}

		try {
			state.scs.printer.print('c');
		} catch (EmptyException e) {
			Main.error("Printer is out of ink and/or paper.");
		} catch (OverloadException e) {
			Main.error("Reached end of line on paper.");
		}
	}
}
