package org.gB.selfcheckout.software.UI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.gB.selfcheckout.software.LoginDB;

public class LoginScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	private AttendantFrame attendantFrame;

	private JLabel loginlabel, passwordlabel;
	public JTextField loginfield;
	public JPasswordField passwordfield;
	private JButton loginbutton;

	public LoginScreen(AttendantFrame attendantFrame, LoginDB logindatabase) {
		this.attendantFrame = attendantFrame;

		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(200, 100));
		JPanel subpanel = new JPanel();
		;
		subpanel.setLayout(new GridBagLayout());
		subpanel.setPreferredSize(new Dimension(200, 100));
		GridBagConstraints c = new GridBagConstraints();

		loginlabel = new JLabel("Username: ", SwingConstants.RIGHT);
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 0;
		c.gridy = 0;
		subpanel.add(loginlabel, c);

		loginfield = new JTextField();
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 0;
		subpanel.add(loginfield, c);

		passwordlabel = new JLabel("Password:", SwingConstants.RIGHT);
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 0;
		c.gridy = 1;
		subpanel.add(passwordlabel, c);

		passwordfield = new JPasswordField();
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		subpanel.add(passwordfield, c);

		loginbutton = new JButton("Login");
		c.ipady = 0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 3;
		loginbutton.addActionListener(e -> {
			String username = new String(loginfield.getText());
			String password = new String(passwordfield.getPassword());
			if (logindatabase.login(username, password)) {
				for (CustomerFrame cf : attendantFrame.cFrames) {
					if (cf.isBeingUsed) {
						cf.cardLayout.show(cf.getContentPane(), "mainScreen");
					} else {
						cf.cardLayout.show(cf.getContentPane(), "startScreen");
					}

				}
				attendantFrame.cardLayout.show(attendantFrame.getContentPane(), "main");
			}
		});
		subpanel.add(loginbutton, c);

		JButton poweroff = new JButton("Power Off");
		c.ipady = 0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(10, 0, 5, 5);
		c.gridx = 0;
		c.gridy = 3;
		poweroff.addActionListener(e -> {
			loginfield.setText("");
			passwordfield.setText("");
			attendantFrame.shutDown.shutDown();
			attendantFrame.cardLayout.show(attendantFrame.getContentPane(), "shutDown");
		});
		subpanel.add(poweroff, c);

		this.add(subpanel);

	}
}
