package org.gB.selfcheckout.software.UI;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
/*
 * Backend integration required: 
 * 	Hardware: 
 * 		access coin and banknote slots
 *  Software:
 *  	amount customerFrame.st.paymentTotal so far
 *  	total amount to pay
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;

public class PayWithCash extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel enterCoins;
	private JLabel enterBanknotes;

	private JButton backButton;
	public CustomerFrame customerFrame;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;
	private JButton finish;

	JButton c5 = new JButton("¢5");
	JButton c10 = new JButton("¢10");
	JButton c25 = new JButton("¢25");
	JButton d1 = new JButton("$1");
	JButton d2 = new JButton("$2");

	JButton b5 = new JButton("$5");
	JButton b10 = new JButton("$10");
	JButton b20 = new JButton("$20");
	JButton b50 = new JButton("$50");

	JLabel paidLabel;
	JLabel totalLabel;

	public PayWithCash(CustomerFrame customerFrame) {

		this.customerFrame = customerFrame;

		// this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		this.setLayout(new GridLayout(2, 1));

		setUpBackButton();

		bottomPanel.setLayout(new GridBagLayout());

		enterCoins = new JLabel("Enter Coins");
		enterCoins.setFont(new Font("serif", Font.PLAIN, 18));
		enterBanknotes = new JLabel("Enter Banknotes");
		enterBanknotes.setFont(new Font("serif", Font.PLAIN, 18));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(enterCoins, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(enterBanknotes, gbc);

		// Set up coin input buttons
		c5.addActionListener(this);
		c10.addActionListener(this);
		c25.addActionListener(this);
		d1.addActionListener(this);
		d2.addActionListener(this);
		b5.addActionListener(this);
		b10.addActionListener(this);
		b20.addActionListener(this);
		b50.addActionListener(this);

		JPanel coinsPanel = new JPanel();
		coinsPanel.setLayout(new GridLayout(3, 2));

		coinsPanel.add(c5);
		coinsPanel.add(c10);
		coinsPanel.add(c25);
		coinsPanel.add(d1);
		coinsPanel.add(d2);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(coinsPanel, gbc);

		// Set up banknote input buttons

		JPanel banknotesPanel = new JPanel();
		banknotesPanel.setLayout(new GridLayout(2, 2));

		banknotesPanel.add(b5);
		banknotesPanel.add(b10);
		banknotesPanel.add(b20);
		banknotesPanel.add(b50);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(banknotesPanel, gbc);

		// Set up bottom buttons
		paidLabel = new JLabel("Paid: $" + customerFrame.st.paymentTotal.floatValue());
		totalLabel = new JLabel("Total: " + customerFrame.st.totalToPay.floatValue());

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(3, 1));

		bottomPanel.add(labelPanel, gbc);
		labelPanel.add(paidLabel);
		labelPanel.add(totalLabel);

		finish = new JButton("Finish");
		finish.addActionListener(this);
		labelPanel.add(finish);
		finish.setVisible(false);

	}

	public void updateLabels() {
		paidLabel.setText(String.format("Paid: $%.2f", customerFrame.st.paymentTotal.floatValue()));
		totalLabel.setText(String.format("Total: $%.2f", customerFrame.st.totalToPay.floatValue()));
		if (customerFrame.st.paymentTotal.subtract(customerFrame.st.totalToPay).floatValue() >= 0.0) {
			finish.setVisible(true);
		}
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
			// Go back to ProceedToPayment Panel
			this.customerFrame.cardLayout.show(this.customerFrame.getContentPane(), "proceedToPay");
		} else if (e.getSource() == c5) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.coinSlot.accept(new Coin(new BigDecimal(0.05)));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == c10) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.coinSlot.accept(new Coin(new BigDecimal(0.10)));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == c25) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.coinSlot.accept(new Coin(new BigDecimal(0.25)));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == d1) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.coinSlot.accept(new Coin(new BigDecimal(1)));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == d2) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.coinSlot.accept(new Coin(new BigDecimal(2)));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == b5) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.banknoteInput.accept(new Banknote(Currency.getInstance("CAD"), 5));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == b10) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.banknoteInput.accept(new Banknote(Currency.getInstance("CAD"), 10));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == b20) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.banknoteInput.accept(new Banknote(Currency.getInstance("CAD"), 20));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == b50) {
			// update amount in customerFrame.st.paymentTotal
			try {
				customerFrame.st.scs.banknoteInput.accept(new Banknote(Currency.getInstance("CAD"), 50));
			} catch (DisabledException | OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			updateLabels();
		} else if (e.getSource() == finish) {
			// TODO: integrate with attendant station
			/*
			 * try {
			 * customerFrame.st.returnChange.returnChange(customerFrame.st.paymentTotal.
			 * subtract(customerFrame.st.totalToPay)); } catch (OverloadException |
			 * EmptyException | DisabledException e1) { // TODO Auto-generated catch block
			 * e1.printStackTrace(); }
			 */
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "thankYou");
		}

	}

}
