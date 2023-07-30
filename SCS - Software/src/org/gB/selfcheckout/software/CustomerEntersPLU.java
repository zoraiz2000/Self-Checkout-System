package org.gB.selfcheckout.software;

import java.util.ArrayList;
import java.util.Set;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class CustomerEntersPLU {
	/**
	 * Looks up the PLU code in the database and returns the product when entered
	 * correctly.
	 * 
	 * @param The PLU code to look up.
	 * @return The single item looked up.
	 **/
	public PLUCodedProduct getEnteredPLUProduct(PriceLookupCode code) {
		if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(code)) {
			return ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
		} else {
			Main.error("Price Lookup Code matched no items in database!");
		}
		return null;
	}

	/**
	 * Looks up the PLU code in the database and returns the list of products
	 * matched so far.
	 * 
	 * @param The partial or complete PLU code to look up.
	 * @return The list of items looked up.
	 **/
	ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();

	public ArrayList<PLUCodedProduct> getEnteredPLUProductList(PriceLookupCode code) {
		boolean track = true;
		Set<PriceLookupCode> keys = ProductDatabases.PLU_PRODUCT_DATABASE.keySet();
		for (PriceLookupCode key : keys) {
			for (int i = 0; i < code.numeralCount(); i++) {
				if (key.getNumeralAt(i) != code.getNumeralAt(i)) {
					track = false;
				}
			}
			if (track == true) {
				productList.add(ProductDatabases.PLU_PRODUCT_DATABASE.get(key));
			}
		}
		return productList;
	}
}