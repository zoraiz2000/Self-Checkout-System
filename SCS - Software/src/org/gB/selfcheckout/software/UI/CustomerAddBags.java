package org.gB.selfcheckout.software.UI;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/* 
 * Backend integration required:
 *  Hardware:
 *  	none
 *  Software:
 *  	number of bags used?
 */

public class CustomerAddBags extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	public CustomerFrame customerFrame;
	private JButton backButton;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;
	private JButton ownBags, useBags;
	private NumericKeypad keypad = new NumericKeypad("Enter # Bags Used");
	JButton enter = new JButton("Enter");

	public CustomerAddBags(CustomerFrame customerFrame) {

		this.customerFrame = customerFrame;
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

		setUpBackButton();

		this.bottomPanel.setLayout(new FlowLayout());

		ownBags = new JButton("Use Own Bags");
		useBags = new JButton("Use Plastic Bags");

		ownBags.addActionListener(this);
		useBags.addActionListener(this);

		bottomPanel.add(ownBags);
		bottomPanel.add(useBags);

		keypad.setEnabled(false);
		keypad.setVisible(false);
		bottomPanel.add(keypad);

		enter.addActionListener(this);
		enter.setVisible(false);
		bottomPanel.add(enter);

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

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == backButton) {
			// Go back to main customer menu
			keypad.setEnabled(false);
			keypad.setVisible(false);
			enter.setVisible(false);
			keypad.enteredInfo = "";
			keypad.txtField.setText("Enter # Bags Used");
			customerFrame.cardLayout.show(this.customerFrame.getContentPane(), "mainScreen");
		} else if (e.getSource() == ownBags) {
			// Alert Attendant with message "Customer at station
			// "+customerFrame.stationIndex+" wants to use their own bag"
			// Backend: block the station?
			customerFrame.cardLayout.show(this.customerFrame.getContentPane(), "blockedScreen");
		} else if (e.getSource() == useBags) {
			keypad.setEnabled(true);
			keypad.setVisible(true);
			enter.setVisible(true);

		} else if (e.getSource() == enter) {
			keypad.setEnabled(false);
			keypad.setVisible(false);
			enter.setVisible(false);
			keypad.enteredInfo = "";
			keypad.txtField.setText("Enter # Bags Used");
			customerFrame.cardLayout.show(this.customerFrame.getContentPane(), "mainScreen");
		}

	}

}
