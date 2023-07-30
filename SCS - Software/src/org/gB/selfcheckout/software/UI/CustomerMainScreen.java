package org.gB.selfcheckout.software.UI;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/*
 * Main screen for user functionality
 * Backend integration required:
 *  Hardware:
 *  	None
 *  Software:
 *  	Total paid so far
 *  	Total to pay
 */

public class CustomerMainScreen extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private CustomerFrame customerFrame;
	private GridBagConstraints gbc = new GridBagConstraints();

	private JButton scan = new JButton("Scan Item");
	private JButton enterCode = new JButton("Enter PLU Code");
	private JButton lookup = new JButton("Look Up Item");
	private JButton remove = new JButton("Remove Item");
	private JButton pay = new JButton("Pay");
	private JButton memeberInfo = new JButton("Enter Member Info");
	private JButton addBags = new JButton("Add Bags");
	public Vector<Product> cartProducts = new Vector<Product>();

	private JLabel paidLabel = new JLabel();
	private JLabel totalLabel = new JLabel();

	// public Map<Product, Integer> productCart = new HashMap<>();
	private JList cartDisplay = new JList();

	public CustomerMainScreen(CustomerFrame cf) {
		super();
		this.customerFrame = cf;

		gbc.insets = new Insets(7, 7, 7, 7);

		this.setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0.75;
		gbc.weightx = 1.0;
		gbc.ipady = 100;
		gbc.fill = GridBagConstraints.BOTH;

		add(cartDisplay, gbc);

		displayProductCart();

		// Show subtotals
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 0.0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;
		gbc.fill = GridBagConstraints.NONE;

		JPanel subtotalsPanel = new JPanel();

		this.add(subtotalsPanel, gbc);
		subtotalsPanel.setLayout(new BoxLayout(subtotalsPanel, BoxLayout.Y_AXIS));

		paidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		paidLabel.setText(String.format("Paid: $%.2f", customerFrame.st.paymentTotal.floatValue()));
		totalLabel.setText(String.format("Total: $%.2f", customerFrame.st.totalToPay.floatValue()));

		subtotalsPanel.add(paidLabel);
		subtotalsPanel.add(totalLabel);

		// Buttons for functionality
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;
		JPanel buttonsPanel = new JPanel();

		this.add(buttonsPanel, gbc);

		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		scan.addActionListener(this);
		scan.setAlignmentX(Component.CENTER_ALIGNMENT);
		enterCode.addActionListener(this);
		enterCode.setAlignmentX(Component.CENTER_ALIGNMENT);
		lookup.addActionListener(this);
		lookup.setAlignmentX(Component.CENTER_ALIGNMENT);
		remove.addActionListener(this);
		remove.setAlignmentX(Component.CENTER_ALIGNMENT);
		addBags.addActionListener(this);
		addBags.setAlignmentX(Component.CENTER_ALIGNMENT);
		memeberInfo.addActionListener(this);
		memeberInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
		pay.addActionListener(this);
		pay.setAlignmentX(Component.CENTER_ALIGNMENT);

		buttonsPanel.add(scan);
		buttonsPanel.add(lookup);
		buttonsPanel.add(enterCode);
		buttonsPanel.add(remove);
		buttonsPanel.add(addBags);
		buttonsPanel.add(memeberInfo);
		buttonsPanel.add(pay);

	}

	public void displayProductCart() {

		cartDisplay.setListData(new Object[0]);
		Vector<String> cartString = new Vector<String>();
		cartProducts.removeAllElements();

		cartString.add("Cart Contains:");

		customerFrame.st.productCart.forEach((product, integer) -> cartProducts.add(product));

		for (Product p : cartProducts) {
			try {
				BarcodedProduct bp = (BarcodedProduct) p;
				cartString
						.add(String.format("%s x %d: $%.2f", bp.getDescription(), customerFrame.st.productCart.get(bp),
								customerFrame.st.productCart.get(bp) * bp.getPrice().floatValue()));
			} catch (Exception e) {
				PLUCodedProduct pp = (PLUCodedProduct) p;
				cartString
						.add(String.format("%s x %d: $%.2f", pp.getDescription(), customerFrame.st.productCart.get(pp),
								customerFrame.st.productCart.get(pp) * pp.getPrice().floatValue()));
			}
		}

		cartDisplay.setListData(cartString);

		paidLabel.setText(String.format("Paid: $%.2f", customerFrame.st.paymentTotal.floatValue()));
		totalLabel.setText(String.format("Total: $%.2f", customerFrame.st.totalToPay.floatValue()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == scan) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "scanItem");
		} else if (e.getSource() == enterCode) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "enterPLU");
		} else if (e.getSource() == lookup) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "lookup");
		} else if (e.getSource() == remove) {
			// Alert attendant with message "Customer at station "+stationIndex+" wants to
			// remove an item"
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "blockedScreen");
		} else if (e.getSource() == addBags) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "addBags");
		} else if (e.getSource() == memeberInfo) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "enterMember");
		} else if (e.getSource() == pay) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "proceedToPay");
		}

	}
}