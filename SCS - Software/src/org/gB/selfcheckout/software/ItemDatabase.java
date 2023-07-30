package org.gB.selfcheckout.software;

import java.util.ArrayList;
import java.util.Map;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.NullPointerSimulationException;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/**
 * Simple database for relating items, products, and barcodes.
 *
 * For testing purposes, can add entries, but cannot remove entries.
 *
 * Further functionality can be added or replaced in later iterations.
 */
public class ItemDatabase {
	// Objects with the same indices are related in the following lists.
	// Null represents no corresponding object for that entry.
	public final ArrayList<Item> itemList = new ArrayList<>(); // Ordered list of items.
	private final ArrayList<Product> productList = new ArrayList<>(); // Ordered list of products.
	private final ArrayList<Barcode> barcodeList = new ArrayList<>(); // Ordered list of barcodes.
	// added
	private static ItemDatabase p = new ItemDatabase();

	public void addBarcodedEntry(Barcode barcode, BarcodedProduct product) {
		if (barcode == null || product == null)
			throw new NullPointerException("Provided barcode or product is null.");
		else
			ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
	}

	public void addPLUCodedEntry(PriceLookupCode pluCode, PLUCodedProduct product) {
		if (pluCode == null || product == null)
			throw new NullPointerException("Provided pluCode or product is null.");
		else
			ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode, product);
	}

	public void removeBarcodedEntry(Barcode barcode) {
		if (barcode == null)
			throw new NullPointerException("Provided barcode or product is null.");
		else
			ProductDatabases.BARCODED_PRODUCT_DATABASE.remove(barcode);
	}

	public void removePLUCodedEntry(PriceLookupCode pluCode) {
		if (pluCode == null)
			throw new NullPointerException("Provided pluCode or product is null.");
		else
			ProductDatabases.PLU_PRODUCT_DATABASE.remove(pluCode);
	}

	// Returns the PLUCodedProduct object if it exists in the database, else returns
	// null
	public PLUCodedProduct getPLUCodedProduct(PriceLookupCode pluCode) {
		if (pluCode == null)
			throw new NullPointerException("Provided pluCode is null.");
		else
			return ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode);
	}

	// Returns the BarcodedProduct object if it exists in the database, else returns
	// null
	public BarcodedProduct getBarcodedProduct(Barcode barcode) {
		if (barcode == null)
			throw new NullPointerException("Provided barcode is null.");
		else
			return ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
	}

	public Map<PriceLookupCode, PLUCodedProduct> getPLUProductDatabase() {
		return ProductDatabases.PLU_PRODUCT_DATABASE;
	}

	public Map<Barcode, BarcodedProduct> getBarcodedProductDatabase() {
		return ProductDatabases.BARCODED_PRODUCT_DATABASE;
	}

	/**
	 * Adds an entry to the database.
	 *
	 * @param item    Item to be added to the database.
	 * @param product Product corresponding to the item to be added.
	 * @return 1 on successful entry, otherwise error.
	 */
	public int addEntry(Item item, Product product) {
		// Ensure lists are not malformed.
		if (itemList.size() != productList.size() && productList.size() != barcodeList.size()) {
			throw new NullPointerSimulationException("Database is malformed.");
		}

		// Get barcode if applicable.
		Barcode barcode;
		if (item instanceof BarcodedItem) {
			barcode = ((BarcodedItem) item).getBarcode();
			// Return error if barcode already exists in the database.
			if (barcodeList.contains(barcode)) {
				return 0;
			}
		} else {
			barcode = null;
		}

		// Add entry.
		itemList.add(item);
		productList.add(product);
		barcodeList.add(barcode);

		return 1;
	}

	/**
	 * Gets an item given its barcode.
	 * 
	 * @param barcode Barcode to search for.
	 * @return Item corresponding to the barcode or NULL if no item found.
	 */
	public Item getItem(Barcode barcode) {
		int searchIndex = barcodeList.indexOf(barcode);
		Item searchItem;
		if (searchIndex == -1) {
			searchItem = null;
		} else {
			searchItem = itemList.get(searchIndex);
		}
		return searchItem;
	}

	/**
	 * Gets a product given its barcode.
	 * 
	 * @param barcode Barcode to search for.
	 * @return Product corresponding to the barcode or NULL if no product found.
	 */
	public Product getProduct(Barcode barcode) {
		int searchIndex = barcodeList.indexOf(barcode);
		Product searchProduct;
		if (searchIndex == -1) {
			searchProduct = null;
		} else {
			searchProduct = productList.get(searchIndex);
		}
		return searchProduct;
	}

	public ItemDatabase getInstance() {
		return p;
	}
}
