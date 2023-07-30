package org.gB.selfcheckout.test;

import java.math.BigDecimal;

import org.gB.selfcheckout.software.ItemDatabase;
import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.ScanItem;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.Product;

/**
 * A test suite for org.g30.selfcheckout.ScanItem.
 */
public class TestScanItem {
	protected boolean enabled = false;

	/**
	 * A stub for the ElectronicScaleObserver class to manipulate the enabled
	 * member.
	 */
	private class StubElectronicScaleObserver implements ElectronicScaleObserver {
		// Notes the enabled method was called by setting the enabled member.
		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
			enabled = true;
		}

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {

		}

		@Override
		public void weightChanged(ElectronicScale scale, double weightInGrams) {

		}

		@Override
		public void overload(ElectronicScale scale) {

		}

		@Override
		public void outOfOverload(ElectronicScale scale) {

		}
	}

	private State state;
	private BarcodeScanner scanner;

	private Item item;
	private Item itemInvalid;
	private Product product;

	@Before
	public void setupTests() {
		// Initialize State.
		try {
			state = Main.init(100, 1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Set variables to respective components of State.
		assert state != null;
		scanner = state.scs.mainScanner;
		ItemDatabase database = state.idb;
		ElectronicScale scale = state.scs.baggingArea;

		// Attach observer stub to scale.
		StubElectronicScaleObserver stubObserver = new StubElectronicScaleObserver();
		scale.attach(stubObserver);

		// Add an entry to database.
		Barcode barcode = new Barcode(new Numeral[] { Numeral.one });
		item = new BarcodedItem(barcode, 1);
		product = new BarcodedProduct(barcode, "product", new BigDecimal(2), 50.0);
		database.addBarcodedEntry(barcode, (BarcodedProduct) product);

		// Create an entry not in database.
		Barcode barcodeInvalid = new Barcode(new Numeral[] { Numeral.four });
		itemInvalid = new BarcodedItem(barcodeInvalid, 7);

		// Initialize test class.
		ScanItem scan = new ScanItem(state);
		scanner.attach(scan);
	}

	@Test
	public void testAddItemToList() {
		scanner.scan(item); // Scan the item.
		// Ensure the item is now available to be removed from the scanned items list.
		Assert.assertTrue(state.removeProduct(product));
	}

	@Test
	public void testEnablesScaleTimeout() {
		scanner.scan(item); // Scan the item.
		// Ensure the scale is expecting the item.
		Assert.assertTrue(state.waitingForBagging);
	}

	@Test
	public void testScaleEnabled() {
		scanner.scan(item); // Scan the item.
		Assert.assertFalse(enabled); // Ensure the scale is enabled.
	}

	@Test
	public void testItemNotInDatabaseDoesNotEnableScaleTimeout() {
		scanner.scan(itemInvalid); // Scan an invalid item.
		// Ensure the scale is not expecting the item.
		Assert.assertFalse(state.waitingForBagging);
	}

	@Test
	public void testItemNotInDatabaseDoesNotEnableScale() {
		scanner.scan(itemInvalid); // Scan an invalid item.
		Assert.assertFalse(enabled); // Ensure the scale is not enabled.
	}

}
