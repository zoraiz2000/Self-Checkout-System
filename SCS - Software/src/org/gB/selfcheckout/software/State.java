package org.gB.selfcheckout.software;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/**
 * Stores the state of the self-checkout program. The members scs, addBanknote,
 * addCoin, addItemToBag, checkout, and scanItem are uninitialized by default.
 */

public class State {
	// Stores all items scanned into the machine.
	public ArrayList<Item> scannedItems = new ArrayList<Item>();

	// We need products in order to get the price for printing the receipt
	// public ArrayList<Product> scannedProducts = new ArrayList<Product>();
	public HashMap<Product, Integer> productCart = new HashMap<>();

	// Stores the number of plastic bags used
	private int plasticBagCount = 0;
	// Stores the weight of all items in scannedItems.
	public double expectedWeight = 0;
	// Stores the weight at the time an item was scanned, excluding it.
	public double previousWeight = 0;
	// Stores the value of the money inserted into the system.
	public BigDecimal paymentTotal = new BigDecimal(0.0);
	// Stores the total price of currently scanned products
	public BigDecimal totalToPay = new BigDecimal(0.0);
	// Indiciates if a card has been inserted.
	public boolean isCardInserted = false;
	public boolean itemScanned = false;

	// Indicates if scanning/bagging or paying is enabled (the latter is true).
	public boolean paymentEnabled = false;
	// Indicates the bagging area scale should wait for the expected weight.
	public boolean waitingForBagging = false;
	// The member number of the customer.
	public String memberNumber = null;
	// The weight of a customer's personal bags.
	public double customerBagWeight = 0.0;

	// States of ink and paper left in the receipt printer hardware
	public int linesOfPaperRemaining = 0;
	public int charactersOfInkRemaining = 0;
	public boolean lowOnInk = true;
	public boolean outOfInk = true;
	public boolean lowOnPaper = true;
	public boolean outOfPaper = true;

	// Stores the payments made for each card transactions.
	public ArrayList<Pair<Card.CardData, BigDecimal>> cardPayments = new ArrayList<Pair<Card.CardData, BigDecimal>>();
	// A database to contain items for purchase.
	public ItemDatabase idb = null;
	// A database to contain card issuer with the corresponding card type
	public CardIssuerDatabase cardIssuerDatabase = CardIssuerDatabase.c;
	// An instance of the self checkout station hardware.
	public SelfCheckoutStation scs = null;
	// Instances of the use case handlers.
	public AddBanknote addBanknote = null;
	public AddCoin addCoin = null;
	public AddItemToBag addItemToBag = null;
	public AddCustomerBags customerBags = null;
	public PaymentMode paymentMode = null;
	public ScanItem scanItem = null;
	public ScanMembershipCard scanMembershipCard = null;
	public ReturnChange returnChange = null;
	public PrintReceipt printReceipt = null;

	public CustomerDoesNotWantToBagScannedItem customerDoesNotWantToBagScannedItem = null;

	// Stores the current power state of the self-checkout station.
	public boolean poweredOn = true;

	// the use case where the membership card information is entered by numpad
	public String membershipCardInfo = "";

	/**
	 * Returns the current expected weight of the scanned items list.
	 * 
	 * @return The value of expectedWeight.
	 */
	public double getExpectedWeight() {
		return expectedWeight;
	}

	/**
	 * Add the specified item to the scanned items list. The expected weight is
	 * updated appropriately.
	 * 
	 * @param product The item to be added to the scanned items list.
	 */
	public void addProduct(Product product) {
		if (productCart.containsKey(product)) {
			// Increment count of that product
			productCart.replace(product, productCart.get(product) + 1);
		} else {
			// Add the new product to productCart
			productCart.put(product, 1);
		}
		totalToPay = totalToPay.add(product.getPrice());
		if (product instanceof BarcodedProduct) {
			expectedWeight += ((BarcodedProduct) product).getExpectedWeight();
		} else if (product instanceof PLUCodedProduct) {
			// PLUCodedProducts are per kilogram
			expectedWeight += 1000;
		}
	}

	/**
	 * Removes the specified item from the scanned items list. The expected weight
	 * is updated appropriately.
	 * 
	 * @param product The item to be removed from the scanned items list.
	 * @return True if the item was removed, false otherwise.
	 */
	public boolean removeProduct(Product product) {
		boolean isProductRemoved = false;
		// If there is more than 1 reduce count of that product, else remove product
		if (productCart.containsKey(product) && productCart.get(product) > 1) {
			productCart.replace(product, productCart.get(product) - 1);
			isProductRemoved = true;
		} else if (productCart.containsKey(product)) {
			productCart.remove(product);
			isProductRemoved = true;
		}

		if (isProductRemoved) {
			totalToPay = totalToPay.subtract(product.getPrice());
			if (product instanceof BarcodedProduct) {
				expectedWeight -= ((BarcodedProduct) product).getExpectedWeight();
			} else if (product instanceof PLUCodedProduct) {
				// PLUCodedProducts are per kilogram
				expectedWeight -= 1000;
			}
		}
		return isProductRemoved;
	}

	/**
	 * Returns the total bag weight on the bagging area scale.
	 * 
	 * @return The value of customerBagWeight.
	 */
	public double getExpectedBagWeight() {
		return customerBagWeight;
	}

	/**
	 * Add the specified bag to bagging area scale. The expected total bag weight is
	 * updated.
	 * 
	 * @param bag The bag to be added.
	 * 
	 * @return False if the scale is overloaded or payment mode is enabled or an
	 *         item is expected to be loaded on the scale. Otherwise returns true.
	 */
	public boolean addBag(Item bag) {
		if (customerBags.overloadFlag || paymentEnabled || waitingForBagging || scs.baggingArea.isDisabled()) {
			return false;
		} else {
			customerBagWeight += bag.getWeight();
			scs.baggingArea.add(bag);
			return true;
		}
	}

	/**
	 * Removes the specified bag from the bagging area scale. The expected total bag
	 * weight is updated.
	 * 
	 * @param bag The bag to be removed.
	 */
	public void removeBag(Item bag) {
		customerBagWeight -= bag.getWeight();
		scs.baggingArea.remove(bag);
	}

	/**
	 * Remove the specified item from the bagging area scale if the due amount is
	 * equal to the amount paid i.e. the scanned items have been purchased.
	 * 
	 * @param item The item to be removed from the bagging area.
	 * @throws OverloadException
	 */
	public boolean removePurchasedItemFromScale(Item item) throws OverloadException {
		// Check if the due amount is paid
		if (totalToPay.compareTo(paymentTotal) == 0) {
			scs.baggingArea.remove(item);
			expectedWeight = scs.baggingArea.getCurrentWeight();
		} else {
			Main.error("Please finish payment before removing items from the bagging area.");
			return false;
		}
		return true;
	}

	/**
	 * Set the total number of plastic bags used by the customer.
	 * 
	 * @param b Number of plastic bags used.
	 * @return True if the a valid number is entered, false otherwise.
	 */
	public boolean setPlasticBagsUsed(int b) {
		if (b < 0) {
			return false;
		}
		plasticBagCount = b;
		return true;
	}

	/**
	 * Get function for plasticBagCount;
	 * 
	 * @return Returns the number of plastic bags used.
	 */
	public int getPlasticBagsUsed() {
		return plasticBagCount;
	}

	/**
	 * Enable all the devices of the self checkout station necessary for scanning
	 * and bagging items, disable all others.
	 */
	public void enableScanning() {
		scs.baggingArea.enable();
		scs.mainScanner.enable();
		scs.handheldScanner.enable();
		scs.printer.disable();
		scs.cardReader.disable();
		scs.banknoteInput.disable();
		scs.banknoteOutput.disable();
		scs.banknoteValidator.disable();
		scs.banknoteStorage.disable();
		for (Map.Entry<Integer, BanknoteDispenser> d : scs.banknoteDispensers.entrySet())
			d.getValue().disable();
		scs.coinSlot.disable();
		scs.coinTray.disable();
		scs.coinValidator.disable();
		scs.coinStorage.disable();
		for (Map.Entry<BigDecimal, CoinDispenser> d : scs.coinDispensers.entrySet())
			d.getValue().disable();
	}

	/**
	 * Enable all the devices of the self checkout station necessary for paying for
	 * a purchase, disable all others.
	 */
	public void enablePayment() {
		scs.baggingArea.disable();
		scs.mainScanner.disable();
		scs.handheldScanner.disable();
		scs.printer.disable();
		scs.cardReader.enable();
		scs.banknoteInput.enable();
		scs.banknoteOutput.enable();
		scs.banknoteValidator.enable();
		scs.banknoteStorage.enable();
		for (Map.Entry<Integer, BanknoteDispenser> d : scs.banknoteDispensers.entrySet())
			d.getValue().enable();
		scs.coinSlot.enable();
		scs.coinTray.enable();
		scs.coinValidator.enable();
		scs.coinStorage.enable();
		for (Map.Entry<BigDecimal, CoinDispenser> d : scs.coinDispensers.entrySet())
			d.getValue().enable();
	}

	/**
	 * Enable all the devices of the self checkout station necessary for
	 * processing/finalizing a purchase, disable all others.
	 */
	public void enableCheckout() {
		scs.baggingArea.enable();
		scs.mainScanner.disable();
		scs.handheldScanner.disable();
		scs.printer.enable();
		scs.cardReader.disable();
		scs.banknoteInput.disable();
		scs.banknoteOutput.disable();
		scs.banknoteValidator.disable();
		scs.banknoteStorage.disable();
		for (Map.Entry<Integer, BanknoteDispenser> d : scs.banknoteDispensers.entrySet())
			d.getValue().disable();
		scs.coinSlot.disable();
		scs.coinTray.disable();
		scs.coinValidator.disable();
		scs.coinStorage.disable();
		for (Map.Entry<BigDecimal, CoinDispenser> d : scs.coinDispensers.entrySet())
			d.getValue().disable();
	}
}
