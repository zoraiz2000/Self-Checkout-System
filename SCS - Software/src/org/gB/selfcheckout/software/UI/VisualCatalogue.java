package org.gB.selfcheckout.software.UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gB.selfcheckout.software.ItemDatabase;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

/* 
 * Backend integration required:
 *  Hardware:
 *  	none
 *  Software:
 *  	List of PLU products in database
 */

public class VisualCatalogue extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static String[] itemOptions;

	public CustomerFrame customerFrame;
	private JButton backButton;
	private GridBagConstraints gbc = new GridBagConstraints();
	private GridBagConstraints gbc2 = new GridBagConstraints();
	private JPanel bottomPanel;
	private ItemDatabase idb = new ItemDatabase();
	private JLabel firstL, secondL, thirdL;
	private JButton firstB, secondB, thirdB, leftArrow, rightArrow;
	int startingOffset = 0;
	private HashMap<String, PLUCodedProduct> indexMap = new HashMap();

	public ArrayList<String> itemMenu = new ArrayList<String>();;

	public VisualCatalogue(CustomerFrame customerFrame) {

		this.customerFrame = customerFrame;
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setUpBackButton();

		setUpItemOptions();

		gbc2.insets = new Insets(10, 10, 10, 10);

		// Set up prompt text
		bottomPanel.setLayout(new GridBagLayout());

		// Set up directional arrows
		leftArrow = new JButton("<");
		leftArrow.addActionListener(this);
		rightArrow = new JButton(">");
		rightArrow.addActionListener(this);
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.ipady = 50;
		gbc2.gridheight = 2;
		bottomPanel.add(leftArrow, gbc2);

		// Set up "thumbnail" buttons
		firstB = new JButton();
		firstB.addActionListener(this);
		secondB = new JButton();
		secondB.addActionListener(this);
		thirdB = new JButton();
		thirdB.addActionListener(this);

		gbc2.gridx = 1;
		gbc2.gridy = 1;
		gbc2.gridheight = 1;
		bottomPanel.add(firstB, gbc2);

		gbc2.gridx = 2;
		gbc2.gridy = 1;
		bottomPanel.add(secondB, gbc2);

		gbc2.gridx = 3;
		gbc2.gridy = 1;
		bottomPanel.add(thirdB, gbc2);

		// Set up product labels

		// Set up "thumbnail" buttons
		firstL = new JLabel(itemMenu.get(0));
		secondL = new JLabel(itemMenu.get(1));
		thirdL = new JLabel(itemMenu.get(2));

		gbc2.gridx = 1;
		gbc2.gridy = 2;
		bottomPanel.add(firstL, gbc2);

		gbc2.gridx = 2;
		gbc2.gridy = 2;
		bottomPanel.add(secondL, gbc2);

		gbc2.gridx = 3;
		gbc2.gridy = 2;
		bottomPanel.add(thirdL, gbc2);

		// Right arrow button
		gbc2.gridx = 4;
		gbc2.gridy = 1;
		gbc2.gridheight = 2;
		gbc2.ipady = 50;
		bottomPanel.add(rightArrow, gbc2);

	}

	public void setUpBackButton() {

		gbc.insets = new Insets(3, 3, 3, 3);

		this.setLayout(new GridBagLayout());

		// Set up back button at the top left
		backButton = new JButton("Back");
		backButton.addActionListener(this);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0.0;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;

		this.add(backButton, gbc);

		// Create another panel for the rest of the screen
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel = new JPanel();
		this.add(bottomPanel, gbc);

	}

	private void setUpItemOptions() {
		// Add barcoded products to drop down menu

		ProductDatabases.PLU_PRODUCT_DATABASE
				.forEach((plu, pluCodedProduct) -> indexMap.put(pluCodedProduct.getDescription(), pluCodedProduct));
		indexMap.forEach((description, product) -> itemMenu.add(description));
	}

	private void updateCatalogue() {
		firstL.setText(itemMenu.get(startingOffset));
		if (itemMenu.size() > startingOffset + 1) {
			secondL.setText(itemMenu.get(startingOffset + 1));
		} else {
			secondL.setText("");
		}
		if (itemMenu.size() > startingOffset + 2) {
			thirdL.setText(itemMenu.get(startingOffset + 2));
		} else {
			thirdL.setText("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == backButton) {
			// Go back to main customer menu
			this.customerFrame.cardLayout.show(this.customerFrame.getContentPane(), "mainScreen");
		} else if (e.getSource() == leftArrow) {
			// Rotate back
			if (startingOffset >= 3) {
				startingOffset -= 3;
			}
			updateCatalogue();
		} else if (e.getSource() == rightArrow) {
			// Rotate forward
			if (itemMenu.size() - startingOffset > 3) {
				startingOffset += 3;
			}
			updateCatalogue();
		} else if (e.getSource() == firstB) {
			// Select product and add to cart - backend integration
			String desc = itemMenu.get(startingOffset);
			PLUCodedProduct pcp = indexMap.get(desc);

			boolean isValidProduct = false;
			double weight = 0.0;

			// new item of that product
			for (Item it : customerFrame.st.idb.getInstance().itemList) {
				if (it instanceof PLUCodedItem) {
					PLUCodedItem pluItem = (PLUCodedItem) it;
					if (pluItem.getPLUCode().equals(pcp.getPLUCode())) {
						isValidProduct = true;
						weight = pluItem.getWeight();
					}
				}
			}

			if (isValidProduct) {
				customerFrame.currentItem = new PLUCodedItem(indexMap.get(desc).getPLUCode(), weight);
				customerFrame.st.addProduct(indexMap.get(desc));
				customerFrame.st.scannedItems.add(customerFrame.currentItem);
				customerFrame.waitingToBag();
			}
		} else if (e.getSource() == secondB) {
			// Select product and add to cart - backend integration
			String desc = itemMenu.get(startingOffset + 1);
			PLUCodedProduct pcp = indexMap.get(desc);

			boolean isValidProduct = false;
			double weight = 0.0;

			// new item of that product
			for (Item it : customerFrame.st.idb.getInstance().itemList) {
				if (it instanceof PLUCodedItem) {
					PLUCodedItem pluItem = (PLUCodedItem) it;
					if (pluItem.getPLUCode().equals(pcp.getPLUCode())) {
						isValidProduct = true;
						weight = pluItem.getWeight();
					}
				}
			}

			if (isValidProduct) {
				customerFrame.currentItem = new PLUCodedItem(indexMap.get(desc).getPLUCode(), weight);
				customerFrame.st.addProduct(indexMap.get(desc));
				customerFrame.waitingToBag();
			}
		} else if (e.getSource() == thirdB) {
			// Select product and add to cart - backend integration
			String desc = itemMenu.get(startingOffset + 2);
			PLUCodedProduct pcp = indexMap.get(desc);

			boolean isValidProduct = false;
			double weight = 0.0;

			// new item of that product
			for (Item it : customerFrame.st.idb.getInstance().itemList) {
				if (it instanceof PLUCodedItem) {
					PLUCodedItem pluItem = (PLUCodedItem) it;
					if (pluItem.getPLUCode().equals(pcp.getPLUCode())) {
						isValidProduct = true;
						weight = pluItem.getWeight();
					}
				}
			}

			if (isValidProduct) {
				customerFrame.currentItem = new PLUCodedItem(indexMap.get(desc).getPLUCode(), weight);
				customerFrame.st.addProduct(indexMap.get(desc));
				customerFrame.waitingToBag();
			}
		}

	}

}
