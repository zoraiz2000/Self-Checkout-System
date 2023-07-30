package org.gB.selfcheckout.software;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.gB.selfcheckout.software.UI.AttendantFrame;
import org.gB.selfcheckout.software.UI.CustomerFrame;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

public class Main {
	static ArrayList<State> states = new ArrayList<State>();
	static ArrayList<CustomerFrame> cFrames = new ArrayList<CustomerFrame>();

	public static State init(int scaleMaxWeight, int scaleSensitivity) throws Exception {
		State state = new SCSMain().init(scaleMaxWeight, scaleSensitivity);
		states.add(state);
		return state;
	}

	public static void error(String message) {
		// System.out.println(message);
	}

	public static void main(String[] args) {
		populateDatabases();
		for (int i = 0; i < 4; i++) {
			try {
				State s = init(100, 1);
				cFrames.add(new CustomerFrame(i, states.get(i)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		AttendantControl ac = new AttendantControl(states);
		AttendantFrame aFrame = new AttendantFrame(states, cFrames, ac);
	}

	private static void populateDatabases() {

		ItemDatabase idbInit = new ItemDatabase();

		PriceLookupCode plu1 = new PriceLookupCode("7654");
		PLUCodedProduct plup1 = new PLUCodedProduct(plu1, "Pepper", new BigDecimal(5.35));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu1, plup1);
		idbInit.getInstance().addEntry(new PLUCodedItem(plu1, 5.5), plup1);

		PriceLookupCode plu2 = new PriceLookupCode("8889");
		PLUCodedProduct plup2 = new PLUCodedProduct(plu2, "Carrot", new BigDecimal(2.25));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu2, plup2);
		idbInit.getInstance().addEntry(new PLUCodedItem(plu2, 3.00), plup2);

		PriceLookupCode plu3 = new PriceLookupCode("9087");
		PLUCodedProduct plup3 = new PLUCodedProduct(plu3, "Orange", new BigDecimal(7.99));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu3, plup3);
		idbInit.getInstance().addEntry(new PLUCodedItem(plu3, 10.10), plup3);

		PriceLookupCode plu4 = new PriceLookupCode("4567");
		PLUCodedProduct plup4 = new PLUCodedProduct(plu4, "Pistachio", new BigDecimal(12.10));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu4, plup4);
		idbInit.getInstance().addEntry(new PLUCodedItem(plu4, 1.5), plup4);

		PriceLookupCode plu5 = new PriceLookupCode("1243");
		PLUCodedProduct plup5 = new PLUCodedProduct(plu5, "Geranium", new BigDecimal(8.50));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu5, plup5);
		idbInit.getInstance().addEntry(new PLUCodedItem(plu5, 25.90), plup5);

		PriceLookupCode plu6 = new PriceLookupCode("2837");
		PLUCodedProduct plup6 = new PLUCodedProduct(plu6, "Seed", new BigDecimal(3.79));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu6, plup6);
		idbInit.getInstance().addEntry(new PLUCodedItem(plu6, 5.75), plup6);

		PriceLookupCode plu7 = new PriceLookupCode("8886");
		PLUCodedProduct plup7 = new PLUCodedProduct(plu7, "Avocado", new BigDecimal(9.98));
		ProductDatabases.PLU_PRODUCT_DATABASE.put(plu7, plup7);
		idbInit.getInstance().addEntry(new PLUCodedItem(plu7, 14.7), plup7);

		Barcode bc1 = new Barcode(
				new Numeral[] { Numeral.five, Numeral.four, Numeral.three, Numeral.two, Numeral.one });
		BarcodedProduct bcp1 = new BarcodedProduct(bc1, "FrootLoops", new BigDecimal(5.35), 15.5);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bc1, bcp1);
		idbInit.getInstance().addEntry(new BarcodedItem(bc1, 15.5), bcp1);

		Barcode bc2 = new Barcode(
				new Numeral[] { Numeral.three, Numeral.two, Numeral.zero, Numeral.nine, Numeral.one });
		BarcodedProduct bcp2 = new BarcodedProduct(bc2, "SaltShaker", new BigDecimal(9.99), 11.3);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bc2, bcp2);
		idbInit.getInstance().addEntry(new BarcodedItem(bc2, 11.3), bcp2);

		Barcode bc3 = new Barcode(
				new Numeral[] { Numeral.nine, Numeral.one, Numeral.zero, Numeral.eight, Numeral.three });
		BarcodedProduct bcp3 = new BarcodedProduct(bc3, "Chips", new BigDecimal(3.00), 8.10);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bc3, bcp3);
		idbInit.getInstance().addEntry(new BarcodedItem(bc3, 8.10), bcp3);

		Barcode bc4 = new Barcode(new Numeral[] { Numeral.four, Numeral.two, Numeral.one, Numeral.two, Numeral.two });
		BarcodedProduct bcp4 = new BarcodedProduct(bc4, "HairBrush", new BigDecimal(13.45), 22.50);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bc4, bcp4);
		idbInit.getInstance().addEntry(new BarcodedItem(bc4, 22.50), bcp4);

	}
}
