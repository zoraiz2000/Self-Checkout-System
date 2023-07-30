package org.gB.selfcheckout.test;

import java.math.BigDecimal;

import org.gB.selfcheckout.software.CustomerDoesNotWantToBagScannedItem;
import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.products.BarcodedProduct;

public class TestCustomerDoesNotWantToBagScannedItem {
	private State state;
	private BarcodedProduct appleProduct;
	private BarcodedProduct heavyProduct;

	@Before
	public void setup() throws Exception {
		state = Main.init(10000, 10);
		CustomerDoesNotWantToBagScannedItem customerDoesNotBagScannedItem = new CustomerDoesNotWantToBagScannedItem(
				state);
		state.scs.baggingArea.attach(customerDoesNotBagScannedItem);
		state.customerDoesNotWantToBagScannedItem = customerDoesNotBagScannedItem;

		Numeral[] numeral1 = new Numeral[2];
		numeral1[0] = Numeral.one;
		numeral1[1] = Numeral.zero; // 10
		Barcode barcode1 = new Barcode(numeral1); // barcode is 10
		this.appleProduct = new BarcodedProduct(barcode1, "Apple", BigDecimal.valueOf(2), 25.0);

		numeral1[0] = Numeral.one;
		numeral1[1] = Numeral.one; // 11
		Barcode barcode2 = new Barcode(numeral1); // barcode is 11
		this.heavyProduct = new BarcodedProduct(barcode2, "Heavy Item", BigDecimal.valueOf(2), 20000);
	}

	@Test
	public void testValidWeightChange() {
		state.customerDoesNotWantToBagScannedItem.enabled(null);
		// Make sure the scale is enabled.
		// "Apple" product, barcode = 10, price = 2, weight = 100.0

		Item a = new Item(25.0) {
		};
		// "Scan" an item.
		state.addProduct(this.appleProduct);
		state.waitingForBagging = false;
		state.scs.mainScanner.disable();
		state.scs.handheldScanner.disable();
		// Put the item on the scale.
		state.scs.baggingArea.add(a);
		// Ensure the state is correctly updated.
		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
	}

	@Test
	public void testValidWeightChangeButBaggingExpected() {
		// Product i = new Product(new BigDecimal(45.6), true) {};

		Item a = new Item(25.0) {
		};
		state.addProduct(this.appleProduct);
		// "Scan" an item.
		// state.addProduct(i);
		state.waitingForBagging = true;
		state.scs.mainScanner.disable();
		state.scs.handheldScanner.disable();
		// Put the item on the scale.
		state.scs.baggingArea.add(a);
		// Ensure the state is correctly updated.
	}

	@Test
	public void testValidDisabledWeightChange() {
		// Product i = new Product(new BigDecimal(45.6), true) {};
		state.addProduct(appleProduct);
		Item a = new Item(25.0) {
		};
		// "Scan" an item.
		// state.addProduct(i);
		state.waitingForBagging = false;
		state.scs.mainScanner.disable();
		state.scs.handheldScanner.disable();
		// Disable the scale.
		state.customerDoesNotWantToBagScannedItem.disabled(null);
		// Put the item on the scale.
		state.scs.baggingArea.add(a);
		// Ensure the state is not changed.
		Assert.assertTrue(state.scs.mainScanner.isDisabled());
		Assert.assertTrue(state.scs.handheldScanner.isDisabled());
	}

	@Test
	public void testInvalidWeightChange() {
		// Product i1 = new Product(new BigDecimal(45.6), true) {};
		Item a2 = new Item(25.0) {
		};
		// "Scan" item 'a'.
		// state.addProduct(i1);

		state.scs.mainScanner.disable();
		state.scs.handheldScanner.disable();
		// "Weight" item 'b'.
		state.scs.baggingArea.add(a2);
		// Ensure the state is not reversed to a scanning mode.
		Assert.assertTrue(state.scs.mainScanner.isDisabled());
		Assert.assertTrue(state.scs.handheldScanner.isDisabled());
	}

	@Test
	public void testUnexpectedChange() {
		state.addProduct(appleProduct);
		Item i = new Item(25.0) {
		};
		state.scs.baggingArea.add(i);
		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
	}

	@Test
	public void testValidAfterOverload() {
		Item a1 = new Item(25.0) {
		};
		Item a2 = new Item(20000) {
		};
		// "Scan" an item.

		state.scs.mainScanner.disable();
		state.scs.handheldScanner.disable();
		// Put a spurious overloading item on the scale.

		state.scs.baggingArea.add(a2);

		Assert.assertTrue(state.scs.mainScanner.isDisabled());
		Assert.assertTrue(state.scs.handheldScanner.isDisabled());

		state.addProduct(appleProduct);
		state.scs.baggingArea.add(a1); // Add the scanned item.

		Assert.assertTrue(state.scs.mainScanner.isDisabled());
		Assert.assertTrue(state.scs.handheldScanner.isDisabled());

		state.scs.baggingArea.remove(a2); // Remove the spurious item.
		// Ensure the state is correctly updated.

		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
	}

	@Test
	public void overloadCycle() {
		state.addProduct(this.heavyProduct);

		Item i = new Item(20000) {
		};
		state.scs.baggingArea.add(i);
		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
		state.scs.baggingArea.remove(i);
		Assert.assertFalse(state.waitingForBagging);
		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
	}

	@Test
	public void overloadCycleButDisabled() {
		state.customerDoesNotWantToBagScannedItem.disabled(null);

		state.addProduct(this.heavyProduct);

		Item i = new Item(20000) {
		};
		state.scs.baggingArea.add(i);
		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
		state.scs.baggingArea.remove(i);
		Assert.assertFalse(state.waitingForBagging);
		Assert.assertFalse(state.scs.mainScanner.isDisabled());
		Assert.assertFalse(state.scs.handheldScanner.isDisabled());
	}

	public void addItemWhileDisabled() {
		state.addProduct(appleProduct);

		Item a = new Item(25.0) {
		};
		state.scs.baggingArea.disable(); // Disable the scale.
		// "Scan" an item.
		// state.addProduct(i);

		state.scs.mainScanner.disable();
		state.scs.handheldScanner.disable();
		state.scs.baggingArea.add(a);

		Assert.assertTrue(state.scs.mainScanner.isDisabled());
		Assert.assertTrue(state.scs.handheldScanner.isDisabled());
	}

}
