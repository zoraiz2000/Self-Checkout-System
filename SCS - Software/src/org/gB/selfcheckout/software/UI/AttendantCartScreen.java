package org.gB.selfcheckout.software.UI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gB.selfcheckout.software.State;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

/**
 * Panel for the Attendant to view a Customer's Cart
 * 
 * Can remove a product from the customer's cart
 * 
 * Based around the list example found on the oracle site:
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDemoProject/src/components/ListDemo.java
 * 
 **/
public class AttendantCartScreen extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private AttendantFrame attendantFrame;
	private State customerState;
	private JList<Entry<Product, Integer>> items;
	private DefaultListModel<Entry<Product, Integer>> itemModel;
	private JButton removeButton, backButton;
	private JList cartDisplay = new JList();
	private int index;

	/**
	 * Generates a JPanel for the Attendant Cart Screen
	 *
	 * @param state The state of the current customer's station
	 */
	public AttendantCartScreen(AttendantFrame attendantFrame, State state, int index) {
		customerState = state;
		this.attendantFrame = attendantFrame;
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(500, 500));
		this.setMinimumSize(new Dimension(200, 200));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		this.index = index - 1;

		itemModel = new DefaultListModel<Entry<Product, Integer>>();
		for (Entry<Product, Integer> entry : customerState.productCart.entrySet()) {
			itemModel.addElement(entry);
		}

		// Create the list and put it in a scroll pane.
		items = new JList<Entry<Product, Integer>>(itemModel);
		items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		items.setSelectedIndex(0);
		items.addListSelectionListener(this);
		items.setVisibleRowCount(10);

		JScrollPane listScrollPane = new JScrollPane(cartDisplay);
		c.weightx = 0.5;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		this.add(listScrollPane, c);

		removeButton = new JButton("Remove");
		RemoveListener removeListener = new RemoveListener();
		removeButton.setActionCommand("Remove");
		removeButton.addActionListener(removeListener);

		backButton = new JButton("Back");
		BackListener backListener = new BackListener();
		backButton.setActionCommand("Back");
		backButton.addActionListener(backListener);

//        String name = itemModel.getElementAt(items.getSelectedIndex()).toString();
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(backButton);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(removeButton);

		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.weightx = 0.8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, 0);
		c.gridx = 1;
		c.gridy = 1;
		this.add(buttonPane, c);
	}

	public void displayProductCart() {

		cartDisplay.setListData(new Object[0]);
		Vector<String> cartString = new Vector<String>();
		Vector<Product> cartProducts = new Vector<Product>();

		cartString.add("Cart Contains:");

		attendantFrame.cFrames.get(index).st.productCart.forEach((product, integer) -> cartProducts.add(product));

		for (Product p : cartProducts) {
			try {
				BarcodedProduct bp = (BarcodedProduct) p;
				cartString.add(String.format("%s x %d: $%.2f", bp.getDescription(),
						attendantFrame.cFrames.get(index).st.productCart.get(bp),
						attendantFrame.cFrames.get(index).st.productCart.get(bp) * bp.getPrice().floatValue()));
			} catch (Exception e) {

				PLUCodedProduct pp = (PLUCodedProduct) p;
				System.out.println("desc: " + pp.getDescription());

				cartString.add(String.format("%s x %d: $%.2f", pp.getDescription(),
						attendantFrame.cFrames.get(index).st.productCart.get(pp),
						attendantFrame.cFrames.get(index).st.productCart.get(pp) * pp.getPrice().floatValue()));
			}
		}

		cartDisplay.setListData(cartString);

	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		if (event.getValueIsAdjusting() == false) {

			if (items.getSelectedIndex() == -1) {
				// No selection, disable fire button.
				removeButton.setEnabled(false);

			} else {
				// Selection, enable the fire button.
				removeButton.setEnabled(true);
			}
		}
	}

	class BackListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			attendantFrame.cardLayout.show(attendantFrame.getContentPane(), "main");
		}
	}

	class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String pstr = cartDisplay.getSelectedValue().toString();
			String pName = pstr.split(" ")[0];
			String pPrice = pstr.split(" ")[3];
			pPrice = pPrice.substring(1);
			float price = Float.parseFloat(pPrice);
			customerState.totalToPay = customerState.totalToPay.subtract(new BigDecimal(price));

			Product removeProduct = null;
			ArrayList<Product> products = new ArrayList<Product>();

			for (Product p : attendantFrame.cFrames.get(index).mainScreen.cartProducts) {
				products.add(p);
			}

			for (Product p1 : products) {
				if (p1 instanceof BarcodedProduct) {
					BarcodedProduct bp = (BarcodedProduct) p1;
					if (bp.getDescription().compareTo(pName) == 0) {
						removeProduct = bp;
					}
				} else {
					PLUCodedProduct pp = (PLUCodedProduct) p1;
					if (pp.getDescription().compareTo(pName) == 0) {
						removeProduct = pp;
					}
				}
			}
			attendantFrame.cFrames.get(index).st.productCart.remove(removeProduct);

			// Display both updated carts
			displayProductCart();
			attendantFrame.cFrames.get(index).mainScreen.displayProductCart();

		}

	}
}
