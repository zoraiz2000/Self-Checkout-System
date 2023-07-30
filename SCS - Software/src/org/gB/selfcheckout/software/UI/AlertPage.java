package org.gB.selfcheckout.software.UI;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JPanel to indicate to the attendant that something needs to be addressed.
 */
public class AlertPage extends JPanel {
	private static final long serialVersionUID = 1L;
	private AttendantFrame attendantFrame;
	private JLabel msg;

	/**
	 * Initializes the alert interface with an empty message.
	 */
	public AlertPage(AttendantFrame attendantFrame) {
		super();
		this.attendantFrame = attendantFrame;
		msg = new JLabel();
		JButton button = new JButton("Accept");
		msg.setBounds(50, 50, 100, 30);
		button.setBounds(50, 100, 100, 30);
		this.add(msg);
		this.add(button);
		button.addActionListener(e -> {
			attendantFrame.cardLayout.show(attendantFrame.getContentPane(), "main");
		});
	}

	/**
	 * Sets the alert message shown on this interface.
	 * 
	 * @param msg The message to be shown.
	 */
	public void setText(String msg) {
		this.msg.setText(msg);
	}
}