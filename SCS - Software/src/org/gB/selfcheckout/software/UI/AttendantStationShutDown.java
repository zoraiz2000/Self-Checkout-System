package org.gB.selfcheckout.software.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * Stubbed class for now, until actual shut down screen is added
 * When attendant starts up a station, this panel will change
 */

public class AttendantStationShutDown extends JPanel implements ActionListener {

	private AttendantFrame attendantFrame;

	public AttendantStationShutDown(AttendantFrame af) {
		super();
		this.attendantFrame = af;

		setBorder(BorderFactory.createEmptyBorder(100, 10, 10, 10));

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.setBackground(Color.GRAY);
		JLabel msg = new JLabel("Attendant Station Shut Down", SwingConstants.CENTER);
		msg.setFont(new Font("serif", Font.PLAIN, 20));
		this.add(msg);

		JButton powerOn = new JButton("Power On");
		add(powerOn);
		powerOn.addActionListener(this);

		msg.setAlignmentX(Component.CENTER_ALIGNMENT);
		powerOn.setAlignmentX(Component.CENTER_ALIGNMENT);

		shutDown();
	}

	public void shutDown() {
		for (CustomerFrame cf : attendantFrame.cFrames) {
			cf.cardLayout.show(cf.getContentPane(), "blockedScreen");
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		attendantFrame.cardLayout.show(attendantFrame.getContentPane(), "login");
	}

}
