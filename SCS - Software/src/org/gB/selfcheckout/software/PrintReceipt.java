package org.gB.selfcheckout.software;

import java.math.BigDecimal;
import java.util.Map;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ReceiptPrinterObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

public class PrintReceipt implements ReceiptPrinterObserver {
	private State state; // Stores a program state for testing.
	private ReceiptPrinter printer;
	private Map<Product, Integer> products;

	public PrintReceipt(State state) {
		this.state = state;
		this.printer = state.scs.printer;
		this.products = state.productCart;
	}

	private void printLetter(char letter) {
		try {
			printer.print(letter);
			if (letter == '\n' && !state.outOfPaper) {
				useLineOfPaper();
			} else if (letter == ' ') {
				// ignore
			} else if (!state.outOfInk) {
				useInk();
			}
		} catch (EmptyException | OverloadException e) {
			Main.error("Printer is out of ink and/or paper.");
		}
	}

	public void printReceipt() {
		String header = String.format("%-4s %-30s %-7s %-5s\n", "Qty", "Product", "Price", "Total");
		String desc = header;
		// Printing quantity, product, price per product, total for that product
		for (Map.Entry<Product, Integer> pair : products.entrySet()) {

			Product product = pair.getKey();
			Integer qty = pair.getValue();

			String prod = "";
			String price = "";
			String total = "";

			if (product instanceof BarcodedProduct) {
				prod = ((BarcodedProduct) product).getDescription();
			} else if (product instanceof PLUCodedProduct) {
				prod = ((PLUCodedProduct) product).getDescription();
			}
			price = product.getPrice().toString();
			total = (product.getPrice().multiply(BigDecimal.valueOf(qty))).toString();

			desc += String.format("%-4d %-30s $ %-5s $ %-5s\n", qty, prod, price, total);
		}

		for (int i = 0; i < desc.length(); i++) {
			printLetter(desc.charAt(i));
		}

		// Print total price for all products
		String totalString = "\nTotal Cost: $" + state.totalToPay.doubleValue() + "\n";
		for (int w = 0; w < totalString.length(); w++) {
			printLetter(totalString.charAt(w));
		}

		// Print total price paid
		String totalPaidString = "Total Paid: $" + state.paymentTotal.doubleValue() + "\n";
		for (int z = 0; z < totalPaidString.length(); z++) {
			printLetter(totalPaidString.charAt(z));
		}

		// Print change due
		Double change = state.paymentTotal.doubleValue() - state.totalToPay.doubleValue();
		String changeString = "Change due: $" + change + "\n";
		for (int q = 0; q < changeString.length(); q++) {
			printLetter(changeString.charAt(q));
		}

		printer.cutPaper();
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void outOfPaper(ReceiptPrinter printer) {
		state.outOfPaper = true;
		state.lowOnPaper = true;
	}

	@Override
	public void outOfInk(ReceiptPrinter printer) {
		state.outOfInk = true;
		state.lowOnPaper = true;

	}

	@Override
	public void paperAdded(ReceiptPrinter printer) {
		updatePaper();
	}

	@Override
	public void inkAdded(ReceiptPrinter printer) {
		updateInk();
	}

	public void useInk() {
		state.charactersOfInkRemaining--;
		updateInk();
	}

	public void useLineOfPaper() {
		state.linesOfPaperRemaining--;
		updatePaper();
	}

	private void updatePaper() {
		if (state.linesOfPaperRemaining > ReceiptPrinter.MAXIMUM_PAPER / 10) {
			state.lowOnPaper = false;
			state.outOfPaper = false;
		}
		if (state.linesOfPaperRemaining <= ReceiptPrinter.MAXIMUM_PAPER / 10) {
			this.state.lowOnPaper = true;
		}
		if (state.linesOfPaperRemaining != 0) {
			this.state.outOfPaper = false;
		}
	}

	private void updateInk() {
		if (state.charactersOfInkRemaining > ReceiptPrinter.MAXIMUM_INK / 10) {
			state.lowOnInk = false;
			state.outOfInk = false;
		}
		if (state.charactersOfInkRemaining <= ReceiptPrinter.MAXIMUM_INK / 10) {
			this.state.lowOnInk = true;
		}
		if (state.charactersOfInkRemaining != 0) {
			this.state.outOfInk = false;
		}
	}

	// These setters are only used for correcting software estimates of ink/paper
	// amount
	public void setPaperRemaining(int paper) {
		state.linesOfPaperRemaining = paper;
	}

	public void setInkRemaining(int ink) {
		state.charactersOfInkRemaining = ink;
	}
}
