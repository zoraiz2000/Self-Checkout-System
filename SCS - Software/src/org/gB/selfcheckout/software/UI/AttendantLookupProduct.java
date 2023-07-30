package org.gB.selfcheckout.software.UI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gB.selfcheckout.software.State;

public class AttendantLookupProduct extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private State st;
	public AttendantFrame attendantFrame;
	private JButton backButton;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;

	public AttendantLookupProduct(AttendantFrame attendantFrame, State state) {
		super();
		this.attendantFrame = attendantFrame;
		this.st = state;
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		setUpBackButton();
		this.bottomPanel.setLayout(new GridLayout(2, 1));

		JLabel lblEnterPluCode = new JLabel("Enter the product's name:");
		bottomPanel.add(lblEnterPluCode);
		JButton BLookup = new JButton("Look up");
		bottomPanel.add(BLookup);
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
			attendantFrame.cardLayout.show(attendantFrame.getContentPane(), "main");
		}
	}

}
