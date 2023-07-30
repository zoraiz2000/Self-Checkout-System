package org.gB.selfcheckout.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.gB.selfcheckout.software.ItemDatabase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/**
 * A test suite for org.g30.selfcheckout.ItemDatabase.
 */
public class TestItemDatabase {

	private ItemDatabase database;
	private Numeral[] numeral1;
	private Numeral[] numeral2;
	private Barcode barcode1;
	private Barcode barcode2;
	private BarcodedProduct appleProduct;
	private BarcodedProduct watermelonProduct;
	private PriceLookupCode pluCode;
	private PLUCodedProduct pluProduct;

	private Barcode barcode;
	private Item item;
	private Product product;

	@Before
	public void setupTests() {
		// Initialize database.
		database = new ItemDatabase();

		// Create entry.
		barcode = new Barcode(new Numeral[] { Numeral.one });
		item = new BarcodedItem(barcode, 1);
		product = new BarcodedProduct(barcode, "product", new BigDecimal(2), 46.5);

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
	}

	// Test adding null barcode for addBarcodedEntry
	@Test(expected = NullPointerException.class)
	public void testAddNullBarcode() {
		database.addBarcodedEntry(null, appleProduct);
	}

	// Test adding null product for addBarcodedEntry
	@Test(expected = NullPointerException.class)
	public void testAddNullProduct() {
		database.addBarcodedEntry(barcode1, null);
	}

	// Test removing null barcode for removeBarcodedEntry
	@Test(expected = NullPointerException.class)
	public void testRemoveNullBarcode() {
		database.removeBarcodedEntry(null);
	}

	// Test adding null PLU code for addPLUCodedEntry
	@Test(expected = NullPointerException.class)
	public void testAddNullPLUCode() {
		database.addPLUCodedEntry(null, pluProduct);
	}

	// Test adding null product for addPLUCodedEntry
	@Test(expected = NullPointerException.class)
	public void testAddNullPLUProduct() {
		database.addPLUCodedEntry(pluCode, null);
	}

	// Test removing null PLU Code for removePLUCodedEntry
	@Test(expected = NullPointerException.class)
	public void testRemoveNullPLUCode() {
		database.removePLUCodedEntry(null);
	}

	// Test null input for getBarcodedProduct
	@Test(expected = NullPointerException.class)
	public void testGetNullBarcode() {
		database.getBarcodedProduct(null);
	}

	// Test null input for getPLUCodedProduct
	@Test(expected = NullPointerException.class)
	public void testGetNullPLUCode() {
		database.getPLUCodedProduct(null);
	}

	// Tests on the item database.
	@Test
	public void testProductDatabases() {
		// Adding a single barcoded product and checking if all it's field are correct
		database.addBarcodedEntry(barcode1, appleProduct);
		Assert.assertEquals(100, database.getBarcodedProduct(barcode1).getExpectedWeight(), 0.001);
		Assert.assertEquals(BigDecimal.valueOf(2), database.getBarcodedProduct(barcode1).getPrice());
		Assert.assertEquals("Apple", database.getBarcodedProduct(barcode1).getDescription());

		// Adding a second barcoded products and checking if all it's field are correct
		database.addBarcodedEntry(barcode2, watermelonProduct);
		Assert.assertEquals(7000, database.getBarcodedProduct(barcode2).getExpectedWeight(), 0.001);
		Assert.assertEquals(BigDecimal.valueOf(10), database.getBarcodedProduct(barcode2).getPrice());
		Assert.assertEquals("Watermelon", database.getBarcodedProduct(barcode2).getDescription());

		// Adding a PLUCodedProduct and checking if all it's field are correct
		database.addPLUCodedEntry(pluCode, pluProduct);
		Assert.assertEquals(BigDecimal.valueOf(3), database.getPLUCodedProduct(pluCode).getPrice());
		Assert.assertEquals("Orange", database.getPLUCodedProduct(pluCode).getDescription());

		// Removing a barcoded product and testing if it's still there
		database.removeBarcodedEntry(barcode1);
		Assert.assertEquals(null, database.getBarcodedProduct(barcode1));

		// Removing a PLUcoded product and testing if it's still there
		database.removePLUCodedEntry(pluCode);
		Assert.assertEquals(null, database.getPLUCodedProduct(pluCode));
	}

	@Test
	public void testGetProductDatabases() {
		Map<PriceLookupCode, PLUCodedProduct> pluProducts = database.getPLUProductDatabase();
		Map<Barcode, BarcodedProduct> barProducts = database.getBarcodedProductDatabase();
		database.addPLUCodedEntry(pluCode, pluProduct);
		database.addBarcodedEntry(barcode1, appleProduct);
		database.addBarcodedEntry(barcode2, watermelonProduct);

		// Iterate through the PLU database and get check product info
		for (Map.Entry<PriceLookupCode, PLUCodedProduct> pair : pluProducts.entrySet()) {
			PriceLookupCode plu = pair.getKey();
			PLUCodedProduct pProduct = pair.getValue();
			for (int i = 0; i < pluProducts.size(); i++) {
				// Check PLU code
				Assert.assertEquals(pluCode, plu);
				// Check product
				Assert.assertEquals(pluProduct, pProduct);
			}
		}

		database.removePLUCodedEntry(pluCode);
		// Iterate through the Barcode database and get check product info
		ArrayList<Barcode> bCodes = new ArrayList<>();
		ArrayList<BarcodedProduct> bProducts = new ArrayList<>();
		bCodes.add(barcode1);
		bCodes.add(barcode2);
		bProducts.add(appleProduct);
		bProducts.add(watermelonProduct);
		for (Map.Entry<Barcode, BarcodedProduct> pair : barProducts.entrySet()) {
			Barcode bcode = pair.getKey();
			BarcodedProduct bProduct = pair.getValue();
			for (int i = 0; i < barProducts.size(); i++) {

				// check that the barcodes in the database are the ones added
				Assert.assertTrue(bCodes.contains(bcode));
				// check that the products in the database are the ones added
				Assert.assertTrue(bProducts.contains(bProduct));
			}
		}
	}

	@Test
	public void testAddFirstValidEntry() {
		// Add a valid entry.
		Assert.assertEquals(database.addEntry(item, product), 1);

		// Ensure entries have been added.
		Assert.assertEquals(database.getItem(barcode).getWeight(), item.getWeight(), 0);
		Assert.assertEquals(database.getProduct(barcode).getPrice(), product.getPrice());
	}

	@Test
	public void testMultipleValidEntry() {
		// Add a valid entry.
		Assert.assertEquals(database.addEntry(item, product), 1);

		// Create and add another valid entry.
		Barcode barcode2 = new Barcode(new Numeral[] { Numeral.two });
		Item item2 = new BarcodedItem(barcode2, 3);
		Product product2 = new BarcodedProduct(barcode2, "product2", new BigDecimal(4), 15.4);
		Assert.assertEquals(database.addEntry(item2, product2), 1);

		// Ensure both entries have been added.
		Assert.assertEquals(database.getItem(barcode).getWeight(), item.getWeight(), 0);
		Assert.assertEquals(database.getProduct(barcode).getPrice(), product.getPrice());

		Assert.assertEquals(database.getItem(barcode2).getWeight(), item2.getWeight(), 0);
		Assert.assertEquals(database.getProduct(barcode2).getPrice(), product2.getPrice());
	}

	@Test
	public void testAddDuplicateBarcode() {
		// Add a valid entry.
		Assert.assertEquals(database.addEntry(item, product), 1);

		// Create another entry with the same barcode.
		Barcode barcodeEq = new Barcode(new Numeral[] { Numeral.one });
		Item itemEq = new BarcodedItem(barcodeEq, 3);
		Product productEq = new BarcodedProduct(barcodeEq, "product", new BigDecimal(4), 33.3);

		// Ensure entry returns error code.
		Assert.assertNotEquals(database.addEntry(itemEq, productEq), 1);

		// Ensure both equal barcodes still point to the first entry.
		Assert.assertEquals(database.getItem(barcode).getWeight(), item.getWeight(), 0);
		Assert.assertEquals(database.getProduct(barcode).getPrice(), product.getPrice());

		Assert.assertEquals(database.getItem(barcodeEq).getWeight(), item.getWeight(), 0);
		Assert.assertEquals(database.getProduct(barcodeEq).getPrice(), product.getPrice());
	}

	@Test
	public void testAddItemWithoutBarcode() {
		// Create a valid entry without a barcode.
		PriceLookupCode code = new PriceLookupCode("1234");
		Item itemPLU = new PLUCodedItem(code, 1);
		Product productPLU = new PLUCodedProduct(code, "product", new BigDecimal(2));

		// Ensure entry is still added.
		Assert.assertEquals(database.addEntry(itemPLU, productPLU), 1);
	}

	@Test
	public void testSearchInvalidBarcode() {
		// Add a valid entry.
		Assert.assertEquals(database.addEntry(item, product), 1);

		// Create a new barcode.
		Barcode barcode2 = new Barcode(new Numeral[] { Numeral.two });

		// Ensure searching new barcode returns null.
		Assert.assertNull(database.getItem(barcode2));
		Assert.assertNull(database.getProduct(barcode2));
	}

	@Test
	public void testGetInstance() {
		ItemDatabase newDatabase = database.getInstance();
		Assert.assertTrue(newDatabase instanceof ItemDatabase);
		Assert.assertEquals(null, newDatabase.getProduct(barcode));
	}

	@After
	public void clean() {
		database.getBarcodedProductDatabase().clear();
		database.getPLUProductDatabase().clear();
	}
}
