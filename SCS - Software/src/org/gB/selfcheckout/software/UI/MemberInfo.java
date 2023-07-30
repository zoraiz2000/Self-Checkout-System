package org.gB.selfcheckout.software.UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * JPanel for the customer to enter their membership number.
 */
public class MemberInfo extends JPanel {
	private static final long serialVersionUID = 1L;

	private CustomerFrame customerFrame;
	private NumericKeypad keypad = new NumericKeypad("Enter Membership Number");
	private JButton backButton;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;

	private JButton enterButton = new JButton("Enter");

	/**
	 * Initializes the interface.
	 * 
	 * @param customerFrame The instance of CustomerFrame that owns this panel.
	 */
	public MemberInfo(CustomerFrame customerFrame) {
		super();
		this.customerFrame = customerFrame;
		this.setLayout(new GridBagLayout());

		setUpBackButton();

		bottomPanel.add(keypad);
		bottomPanel.add(enterButton);

		// Store the entered member number and return to the main screen.
		enterButton.addActionListener(e -> {
			customerFrame.st.memberNumber = keypad.enteredInfo;
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "mainScreen");
		});
	}

	public void setUpBackButton() {

		gbc.insets = new Insets(3, 3, 3, 3);

		this.setLayout(new GridBagLayout());

		// Set up back button at the top left
		backButton = new JButton("Back");
		backButton.addActionListener(e -> {
			// Go back to main customer menu
			keypad.enteredInfo = "";
			keypad.txtField.setText("Enter Membership Number");
			customerFrame.cardLayout.show(customerFrame.getContentPane(), "mainScreen");
		});

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

}
