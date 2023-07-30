package org.gB.selfcheckout.test;

import java.math.BigDecimal;

import org.gB.selfcheckout.software.ItemDatabase;
import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.products.BarcodedProduct;

@SuppressWarnings("deprecation")

/**
 * A test suite for org.g30.selfcheckout.State.
 */
public class TestState {
	State st;
	private ItemDatabase database;
	private Numeral[] numeral1;
	private Numeral[] numeral2;
	private Barcode barcode1;
	private Barcode barcode2;
	private BarcodedProduct p1;
	private BarcodedProduct p2;

	// Create the state and test Products.
	@Before
	public void setup() throws Exception {
		st = new State();

		// Initialize database.
		database = new ItemDatabase();

		numeral1 = new Numeral[2];
		numeral1[0] = Numeral.one;
		numeral1[1] = Numeral.zero; // 10
		barcode1 = new Barcode(numeral1);
		p1 = new BarcodedProduct(barcode1, "Apple", BigDecimal.valueOf(2), 100.0);

		numeral2 = new Numeral[2];
		numeral2[0] = Numeral.one;
		numeral2[1] = Numeral.one; // 11
		barcode2 = new Barcode(numeral2);
		p2 = new BarcodedProduct(barcode2, "Watermelon", BigDecimal.valueOf(10), 2500.0);
	}

	@Test
	public void testAddProduct() {
		st.addProduct(p1);
		Assert.assertEquals(BigDecimal.valueOf(2), st.totalToPay);
		Assert.assertEquals(100.0, st.expectedWeight, 0.001);

		st.addProduct(p1);
		Assert.assertEquals(BigDecimal.valueOf(4), st.totalToPay);
		Assert.assertEquals(200.0, st.expectedWeight, 0.001);
	}

	// Ensure the expected weight is correctly deducted.
	@Test
	public void testRemoveProduct() {
		st.addProduct(p1);
		Assert.assertEquals(BigDecimal.valueOf(2), st.totalToPay);
		Assert.assertEquals(100.0, st.expectedWeight, 0.001);

		st.removeProduct(p1);
		Assert.assertEquals(BigDecimal.valueOf(0), st.totalToPay);
		Assert.assertEquals(0, st.expectedWeight, 0.001);
	}

	// Ensure the expected weight is not deducted from unnecessarily.
	@Test
	public void TestRemoveAbsentProduct() throws Exception {
		st.addProduct(p1);
		Assert.assertEquals(st.removeProduct(p2), false);
		Assert.assertEquals(100.0, st.expectedWeight, 0.001);
	}

	// Ensure that an item can be removed if full payment is done
	@Test
	public void TestRemovePurchasedItem() throws Exception {
		State state = Main.init(100, 10);
		Item i = new Item(50) {
		};
		// adds item to scale
		state.scs.baggingArea.add(i);
		state.totalToPay = new BigDecimal(10);
		state.paymentTotal = new BigDecimal(10);
		Assert.assertTrue(state.removePurchasedItemFromScale(i));
	}

	// Ensure that an item cannot be removed if full payment is not done
	@Test
	public void TestRemoveUnpurchasedItem() throws Exception {
		State state = Main.init(100, 10);
		Item i = new Item(50) {
		};
		// adds item to scale
		state.scs.baggingArea.add(i);
		state.totalToPay = new BigDecimal(10);
		Assert.assertFalse(state.removePurchasedItemFromScale(i));
	}

	@Test
	public void TestEnterPlasticBagsUsed() {
		Assert.assertFalse(st.setPlasticBagsUsed(-1));
		Assert.assertTrue(st.setPlasticBagsUsed(0));
		Assert.assertTrue(st.setPlasticBagsUsed(1));
		Assert.assertEquals(st.getPlasticBagsUsed(), 1);
	}
}
