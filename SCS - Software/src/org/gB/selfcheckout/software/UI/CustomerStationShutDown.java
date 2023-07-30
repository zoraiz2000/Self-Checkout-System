package org.gB.selfcheckout.software.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * Stubbed class for now, until actual shut down screen is added
 * When attendant starts up a station, this panel will change
 */

public class CustomerStationShutDown extends JPanel {

	private CustomerFrame customerFrame;

	public CustomerStationShutDown(CustomerFrame cf) {
		super();
		this.customerFrame = cf;

		this.setBackground(Color.GRAY);
		JLabel msg = new JLabel("Station Shut Down", SwingConstants.CENTER);
		msg.setFont(new Font("serif", Font.PLAIN, 20));
		this.setLayout(new GridLayout(1, 1));
		this.add(msg);
	}

}
