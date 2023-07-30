package org.gB.selfcheckout.test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.gB.selfcheckout.software.CustomerEntersPLU;
import org.junit.Test;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class TestCustomerLooksUpPLU {

	@Test
	public void LookUpOnEmptyPLUDatabase() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		PriceLookupCode code = new PriceLookupCode("12345");
		CustomerEntersPLU process = new CustomerEntersPLU();
		assertEquals(null, process.getEnteredPLUProduct(code));
	}

	@Test
	public void LookUpOnNonEmptyWrongPLUDatabase() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		PriceLookupCode dataCode = new PriceLookupCode("00000");
		PriceLookupCode wrongCode = new PriceLookupCode("12345");
		PLUCodedProduct product = new PLUCodedProduct(dataCode, "description", new BigDecimal(1.0));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(product.getPLUCode(), product);

		CustomerEntersPLU process = new CustomerEntersPLU();
		assertEquals(null, process.getEnteredPLUProduct(wrongCode));
	}

	@Test
	public void LookUpOnNonEmptyCorrectPLUDatabase() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		PriceLookupCode dataCode = new PriceLookupCode("12345");
		PriceLookupCode wrongCode = new PriceLookupCode("12345");
		PLUCodedProduct product = new PLUCodedProduct(dataCode, "description", new BigDecimal(1.0));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(product.getPLUCode(), product);

		CustomerEntersPLU process = new CustomerEntersPLU();
		assertEquals(product, process.getEnteredPLUProduct(wrongCode));
	}

	@Test
	public void LookUpOnEmptyPLUDatabaseList() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();
		PriceLookupCode code = new PriceLookupCode("12345");
		CustomerEntersPLU process = new CustomerEntersPLU();
		assertEquals(productList, process.getEnteredPLUProductList(code));
	}

	@Test
	public void LookUpOnNonEmptyWrongPLUDatabaseList() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();
		PriceLookupCode dataCode = new PriceLookupCode("00000");
		PriceLookupCode wrongCode = new PriceLookupCode("12345");
		PLUCodedProduct rightProduct = new PLUCodedProduct(dataCode, "cheese", new BigDecimal(1.0));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(rightProduct.getPLUCode(), rightProduct);

		CustomerEntersPLU process = new CustomerEntersPLU();
		assertEquals(productList, process.getEnteredPLUProductList(wrongCode));
	}

	@Test
	public void LookUpOnNonEmptyCorrectPLUDatabaseList() {
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>();
		PriceLookupCode dataCode = new PriceLookupCode("12345");
		PLUCodedProduct product = new PLUCodedProduct(dataCode, "description", new BigDecimal(1.0));
		productList.add(product);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(product.getPLUCode(), product);

		CustomerEntersPLU process = new CustomerEntersPLU();
		assertEquals(productList, process.getEnteredPLUProductList(dataCode));
	}
}
