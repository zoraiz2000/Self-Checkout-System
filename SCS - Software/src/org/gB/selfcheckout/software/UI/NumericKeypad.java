
package org.gB.selfcheckout.software.UI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class NumericKeypad extends JPanel {

	private static final long serialVersionUID = 1L;

	public JTextField txtField;
	private JPanel numPanel = new JPanel();
	private JPanel otherPanel = new JPanel();

	public String enteredInfo = "";

	public NumericKeypad(String msg) {
		super();

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		setLayout(new GridLayout(3, 1));

		txtField = new JTextField();
		txtField.setText(msg);
		txtField.setHorizontalAlignment(SwingConstants.CENTER);
		txtField.setColumns(22);
		txtField.setPreferredSize(new Dimension(300, 30));

		add(txtField);

		numPanel.setLayout(new GridLayout(2, 5));
		add(numPanel);

		JButton B1 = new JButton("1");
		B1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "1";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B1);

		JButton B2 = new JButton("2");
		B2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "2";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B2);

		JButton B3 = new JButton("3");
		B3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "3";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B3);

		JButton B4 = new JButton("4");
		B4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "4";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B4);

		JButton B5 = new JButton("5");
		B5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "5";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B5);

		JButton B6 = new JButton("6");
		B6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "6";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B6);

		JButton B7 = new JButton("7");
		B7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "7";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B7);

		JButton B8 = new JButton("8");
		B8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "8";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B8);

		JButton B9 = new JButton("9");
		B9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "9";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B9);

		JButton B0 = new JButton("0");
		B0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + "0";
				txtField.setText(enteredInfo);
			}
		});
		numPanel.add(B0);

		otherPanel.setLayout(new GridLayout(1, 2));

		JButton BBackspace = new JButton("Erase");
		add(BBackspace);
		BBackspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = "";
				txtField.setText(enteredInfo);
			}
		});

		JButton BDecimal = new JButton(".");
		BDecimal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enteredInfo = enteredInfo + ".";
				txtField.setText(enteredInfo);
			}
		});
		add(BDecimal);

		otherPanel.add(BBackspace);
		otherPanel.add(BDecimal);

		add(otherPanel);

	}

}
