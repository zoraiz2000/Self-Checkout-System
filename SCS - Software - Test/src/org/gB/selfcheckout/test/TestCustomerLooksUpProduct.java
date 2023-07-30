package org.gB.selfcheckout.test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.gB.selfcheckout.software.CustomerLooksUpProduct;
import org.junit.Test;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class TestCustomerLooksUpProduct {

	@Test
	public void LookUpOnEmptyProductDatabase() {
		ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();
		CustomerLooksUpProduct process = new CustomerLooksUpProduct();
		assertEquals(productList, process.customerLooksUpPLUProduct("Cheese"));
	}

	@Test
	public void LookUpOnNonEmptyWrongProductDatabase() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();
		PriceLookupCode dataCode = new PriceLookupCode("00000");
		PLUCodedProduct product = new PLUCodedProduct(dataCode, "cheese", new BigDecimal(1.0));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(product.getPLUCode(), product);

		CustomerLooksUpProduct process = new CustomerLooksUpProduct();
		assertEquals(productList, process.customerLooksUpPLUProduct("chicken"));
	}

	@Test
	public void LookUpOnNonEmptyCorrectProductDatabase() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();
		PriceLookupCode dataCode = new PriceLookupCode("12345");
		PLUCodedProduct product = new PLUCodedProduct(dataCode, "cheese", new BigDecimal(1.0));
		productList.add(product);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(product.getPLUCode(), product);

		CustomerLooksUpProduct process = new CustomerLooksUpProduct();
		assertEquals(productList, process.customerLooksUpPLUProduct("cheese"));
	}
}
