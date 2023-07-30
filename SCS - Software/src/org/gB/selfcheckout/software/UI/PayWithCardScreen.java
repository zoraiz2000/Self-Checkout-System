package org.gB.selfcheckout.software.UI;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PayWithCardScreen extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel enterCard;
	private NumericKeypad keypad = new NumericKeypad("Enter PIN");
	private JPanel panel;

	private JButton backButton;
	public CustomerFrame customerFrame;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;
	private JButton swipe, tap, insert, enter;

	public PayWithCardScreen(CustomerFrame customerFrame) {

		this.customerFrame = customerFrame;

		// setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		setLayout(new GridLayout(2, 1));

		setUpBackButton();

		bottomPanel.setLayout(new GridBagLayout());

		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.gridx = 0;
		gbc.gridy = 0;

		enterCard = new JLabel("Please swipe/tap/insert your card", SwingConstants.CENTER);
		enterCard.setFont(new Font("serif", Font.PLAIN, 20));

		bottomPanel.add(enterCard, gbc);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		swipe = new JButton("(SWIPE)");
		tap = new JButton("(TAP)");
		insert = new JButton("(INSERT)");
		swipe.addActionListener(this);
		tap.addActionListener(this);
		insert.addActionListener(this);
		buttonPanel.add(swipe);
		buttonPanel.add(tap);
		buttonPanel.add(insert);

		gbc.gridx = 0;
		gbc.gridy = 1;

		bottomPanel.add(buttonPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 0.5;

		keypad.setEnabled(false);
		keypad.setVisible(false);
		bottomPanel.add(keypad, gbc);
		enter = new JButton("Enter");
		enter.addActionListener(this);
		enter.setVisible(false);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weighty = 0.0;
		bottomPanel.add(enter, gbc);

	}

	public void setUpBackButton() {

		gbc.insets = new Insets(3, 3, 3, 3);
		setLayout(new GridBagLayout());

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
			// Go back to ProceedToPayment Panel
			keypad.setVisible(false);
			keypad.txtField.setText("Enter PIN");
			keypad.enteredInfo = "";
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "proceedToPay");
		} else if (e.getSource() == insert) {
			// ask for PIN
			keypad.setEnabled(true);
			keypad.setVisible(true);
			enter.setVisible(true);
		} else if (e.getSource() == swipe) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "paymentAmount");
		} else if (e.getSource() == tap) {
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "paymentAmount");
		} else if (e.getSource() == enter) {
			// check PIN

			// if pin is okay:
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "paymentAmount");
		}

	}

}
