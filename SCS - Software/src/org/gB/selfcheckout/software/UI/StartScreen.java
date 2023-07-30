package org.gB.selfcheckout.software.UI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/*
 * Screen shown when the station is powered on, but no customer has started yet
 */

public class StartScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private CustomerFrame customerFrame;

	public StartScreen(CustomerFrame cf) {
		super();
		this.customerFrame = cf;

		JButton startButton = new JButton("START");
		startButton.addActionListener(this);
		setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(startButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		customerFrame.isBeingUsed = true;
		customerFrame.cardLayout.show(customerFrame.getContentPane(), "mainScreen");
	}
}
