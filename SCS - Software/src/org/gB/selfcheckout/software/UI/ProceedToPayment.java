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

/*
 * Backend integration required: none
 */

public class ProceedToPayment extends JPanel implements ActionListener {

	public CustomerFrame customerFrame;
	private JButton backButton;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;

	private static final long serialVersionUID = 1L;
	private JLabel paymentMethod;
	private JButton credit;
	private JButton debit;
	private JButton giftCard;
	private JButton cash;

	private JPanel buttonPanel;

	public ProceedToPayment(CustomerFrame customerFrame) {

		this.customerFrame = customerFrame;
		// setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

		setUpBackButton();

		bottomPanel.setLayout(new GridLayout(2, 1));

		paymentMethod = new JLabel("Select a payment method", SwingConstants.CENTER);
		paymentMethod.setFont(new Font("serif", Font.PLAIN, 20));
		bottomPanel.add(paymentMethod);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		bottomPanel.add(buttonPanel);

		credit = new JButton("Credit");
		debit = new JButton("Debit");
		giftCard = new JButton("Gift Card");
		cash = new JButton("Cash");

		buttonPanel.add(credit);
		buttonPanel.add(debit);
		buttonPanel.add(giftCard);
		buttonPanel.add(cash);

		credit.addActionListener(this);
		debit.addActionListener(this);
		giftCard.addActionListener(this);
		cash.addActionListener(this);

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
		add(backButton, gbc);

		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel = new JPanel();
		add(bottomPanel, gbc);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == credit || e.getSource() == debit || e.getSource() == giftCard) {
			// enable payment mode
			customerFrame.st.paymentMode.enablePaymentMode();
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "payWithCard");
		} else if (e.getSource() == cash) {
			// enable payment mode
			customerFrame.st.paymentMode.enablePaymentMode();
			customerFrame.payWithCash.updateLabels();
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "payWithCash");
		} else if (e.getSource() == backButton) {
			// disable payment mode
			customerFrame.mainScreen.displayProductCart();
			customerFrame.st.paymentMode.disablePaymentMode();
			customerFrame.cardLayout.show(this.customerFrame.getContentPane(), "mainScreen");
		}

	}

}
