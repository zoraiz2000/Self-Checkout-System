package org.gB.selfcheckout.software;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

//The goal here is to find a product given its lookup code
public class CustomerLooksUpProduct {
	ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();

	public ArrayList<PLUCodedProduct> customerLooksUpPLUProduct(String partialLookUpName) {
		boolean track = true;
		char[] charArray = partialLookUpName.toCharArray();

		for (Entry<PriceLookupCode, PLUCodedProduct> entry : ProductDatabases.PLU_PRODUCT_DATABASE.entrySet()) {
			PriceLookupCode k = entry.getKey();
			PLUCodedProduct v = entry.getValue();
			if (v.getDescription().length() < partialLookUpName.length()) {
				track = false;
			} else {
				for (int i = 0; i < partialLookUpName.length(); i++) {
					if (v.getDescription().charAt(i) != charArray[i]) {
						track = false;
					}
				}
				if (track == true) {
					productList.add(ProductDatabases.PLU_PRODUCT_DATABASE.get(k));
				}
			}
		}
		return productList;
	}
}
