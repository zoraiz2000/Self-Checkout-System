package org.gB.selfcheckout.software.UI;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * Stubbed class for now, until actual blocked screen is added
 */

public class BlockedScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	private CustomerFrame customerFrame;

	public BlockedScreen(CustomerFrame cf) {
		super();
		this.customerFrame = cf;

		JLabel msg = new JLabel("Please wait for attendant.", SwingConstants.CENTER);
		msg.setFont(new Font("serif", Font.PLAIN, 20));
		this.setLayout(new GridLayout(1, 1));
		this.add(msg);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
