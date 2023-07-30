
package org.gB.selfcheckout.software.UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;

public class EnterPLUCode extends JPanel implements ActionListener {

	public CustomerFrame customerFrame;
	private JButton backButton;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;
	private NumericKeypad keypad = new NumericKeypad("Enter PLU Code");
	private JButton enterButton = new JButton("Enter");

	public EnterPLUCode(CustomerFrame customerFrame) {

		this.customerFrame = customerFrame;
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setUpBackButton();

//		this.bottomPanel.setLayout(new GridLayout(2,1));

		bottomPanel.add(keypad);
		bottomPanel.add(enterButton);
		enterButton.addActionListener(this);
	}

	public void setUpBackButton() {

		gbc.insets = new Insets(3, 3, 3, 3);

		this.setLayout(new GridBagLayout());

		backButton = new JButton("Back");
		backButton.addActionListener(this);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0.0;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;

		backButton.addActionListener(this);
		this.add(backButton, gbc);

		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel = new JPanel();
		this.add(bottomPanel, gbc);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			keypad.enteredInfo = "";
			keypad.txtField.setText("Enter PLU Code");
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "mainScreen");
		} else if (e.getSource() == enterButton) {
			// TODO: add item to cart
			// Only valid PLU codes are 4 numbers long
			if (keypad.enteredInfo.toCharArray().length == 4) {
				boolean isValidProduct = false;
				double weight = 0.0;
				String pluCode = keypad.enteredInfo;

				// new item of that product
				for (Item it : customerFrame.st.idb.getInstance().itemList) {
					if (it instanceof PLUCodedItem) {
						PLUCodedItem pluItem = (PLUCodedItem) it;
						if (pluItem.getPLUCode().toString().compareTo(pluCode) == 0) {
							isValidProduct = true;
							weight = pluItem.getWeight();
						}
					}
				}
				if (isValidProduct) {
					customerFrame.currentItem = new PLUCodedItem(new PriceLookupCode(pluCode), weight);
					customerFrame.st.scannedItems.add(customerFrame.currentItem);
					customerFrame.st.addProduct(
							customerFrame.st.idb.getInstance().getPLUCodedProduct(new PriceLookupCode(pluCode)));
					customerFrame.waitingToBag();
					keypad.enteredInfo = "";
					keypad.txtField.setText("Enter PLU Code");
				}
			}
		}
	}

}
