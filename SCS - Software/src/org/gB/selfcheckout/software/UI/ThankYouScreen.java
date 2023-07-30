package org.gB.selfcheckout.software.UI;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JPanel for the customer to enter their membership number.
 */
public class ThankYouScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	private CustomerFrame customerFrame;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JLabel thankYou = new JLabel("Thank you for shopping with us!");
	private JLabel removeBags = new JLabel("Please remove your bags and take your change and receipt.");

	private JButton next = new JButton("Next Customer");

	/**
	 * Initializes the interface.
	 * 
	 * @param customerFrame The instance of CustomerFrame that owns this panel.
	 */
	public ThankYouScreen(CustomerFrame customerFrame) {
		super();
		this.customerFrame = customerFrame;
		this.setLayout(new GridBagLayout());

		thankYou.setFont(new Font("serif", Font.PLAIN, 20));

		gbc.insets = new Insets(3, 3, 3, 3);

		gbc.gridx = 0;
		gbc.gridy = 0;
		add(thankYou, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(removeBags, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(next, gbc);

		next.addActionListener(e -> {
			// Tell attendant station to restart this station
			customerFrame.st.printReceipt.printReceipt();
			customerFrame.isBeingUsed = false;
			customerFrame.st.expectedWeight = 0;
			customerFrame.st.productCart = new HashMap<>();
			customerFrame.st.totalToPay = new BigDecimal(0.0);
			customerFrame.st.paymentTotal = new BigDecimal(0.0);
			customerFrame.mainScreen.displayProductCart();
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "startScreen");
		});
	}

}
