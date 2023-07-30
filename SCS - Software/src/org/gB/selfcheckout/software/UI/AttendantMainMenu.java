package org.gB.selfcheckout.software.UI;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.gB.selfcheckout.software.State;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;

/**
 * JPanel that implements the main menu interface of the attendant UI.
 */
public class AttendantMainMenu extends JPanel {
	private static final long serialVersionUID = 1L;
	private AttendantFrame attendantFrame;
	private BorderLayout mainBorder = new BorderLayout(); // Outermost layout.
	// Top contents that contains controls for each self-checkout station:
	private JTabbedPane tabs = new JTabbedPane();
	// Bottom layout for navigation controls:
	private JPanel bottomPanel = new JPanel();
	private JButton logoutButton = new JButton("Logout");

	/**
	 * Initializes the main menu interface with the specified number of
	 * self-checkout stations.
	 * 
	 * @param stations       The number of self-checkout stations that this
	 *                       attendant manages.
	 * @param attendantFrame The instance of AttendantFrame that owns this panel.
	 */
	public AttendantMainMenu(AttendantFrame attendantFrame, List<State> states) {
		super();
		this.attendantFrame = attendantFrame;
		this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		this.setLayout(mainBorder); // Set the outermost layout.
		// Create the tabs to manage each self-checkout station:
		for (int i = 0; i < states.size(); i++)
			tabs.addTab("Station " + Integer.toString(i + 1), new StationInterface(i, states.get(i)));
		this.add(tabs, BorderLayout.NORTH);

		// Instantiate the navigation button, add them to the bottom panel:
		bottomPanel.add(logoutButton);
		this.add(bottomPanel, BorderLayout.SOUTH);

		// Setup event handlers:
		logoutButton.addActionListener(e -> {
			attendantFrame.shutDown.shutDown();
			attendantFrame.login.loginfield.setText("");
			attendantFrame.login.passwordfield.setText("");
			attendantFrame.cardLayout.show(attendantFrame.getContentPane(), "login");
		});
	}

	/**
	 * JPanel that implements the attendant controls of a single self-checkout
	 * system.
	 */
	private class StationInterface extends JPanel {
		private static final long serialVersionUID = 1L;
		private BoxLayout layout; // Outermost layout.
		// General management items:
		private JPanel managePanel = new JPanel();
		private JButton power = new JButton("Power Station Off");
		private JButton blockStation = new JButton("Block");
		private JButton unblockStation = new JButton("Unblock");
		private JButton viewCart = new JButton("View Scanned Items");
		// Printer management:
		private JPanel printerPanel = new JPanel();
		private JButton refillPaper = new JButton("Refill Printer Paper");
		private JButton refillInk = new JButton("Refill Printer Ink");
		// Money storage management:
		private JPanel emptyMoneyPanel = new JPanel();
		private JButton emptyCoins = new JButton("Empty Coin Storage");
		private JButton emptyBanknotes = new JButton("Empty Banknote Storage");
		// Money refill panel:
		private JPanel refillMoneyPanel = new JPanel();
		private JButton refillCoinsButton = new JButton("Refill Coins");
		private JButton refillBanknotesButton = new JButton("Refill Banknotes");
		private JComboBox<String> refillCoins = new JComboBox<String>();
		private JComboBox<String> refillBanknotes = new JComboBox<String>();
		// The index of the associated self-checkout station.
		private int stationIndex;
		private State st;

		/**
		 * Initialize the self-checkout station attendant controls, relating the
		 * controls to the station with the specified index.
		 * 
		 * @param index The "number" of the self-checkout station whose attendant
		 *              controls will be manipulated with this instance.
		 */
		public StationInterface(int index, State state) {
			super();
			// Set the station number and main layout.
			stationIndex = index;
			st = state;
			layout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(layout);
			// Setup the general UI:
			managePanel.add(new JLabel("General"));
			managePanel.add(power);
			managePanel.add(blockStation);
			managePanel.add(unblockStation);
			managePanel.add(viewCart);
			this.add(managePanel);
			// Setup the printer handling UI:
			printerPanel.add(new JLabel("Recipt Printer Tools"));
			printerPanel.add(refillPaper);
			printerPanel.add(refillInk);
			this.add(printerPanel);
			// Setup the money handling UI:
			refillCoins.addItem("$0.05");
			refillCoins.addItem("$0.10");
			refillCoins.addItem("$0.25");
			refillCoins.addItem("$1.00");
			refillCoins.addItem("$2.00");
			refillBanknotes.addItem("$5.00");
			refillBanknotes.addItem("$10.00");
			refillBanknotes.addItem("$20.00");
			refillBanknotes.addItem("$50.00");
			emptyMoneyPanel.add(new JLabel("Empty Money"));
			emptyMoneyPanel.add(emptyCoins);
			emptyMoneyPanel.add(emptyBanknotes);
			this.add(emptyMoneyPanel);
			refillMoneyPanel.add(new JLabel("Restock Money"));
			refillMoneyPanel.add(refillCoins);
			refillMoneyPanel.add(refillCoinsButton);
			refillMoneyPanel.add(refillBanknotes);
			refillMoneyPanel.add(refillBanknotesButton);
			this.add(refillMoneyPanel);

			// Set up event handlers:
			power.addActionListener(e -> {
				CustomerFrame cFrame = attendantFrame.cFrames.get(stationIndex);
				if (power.getText().compareTo("Power Station Off") == 0) {
					cFrame.cardLayout.show(cFrame.getContentPane(), "shutDown");
					power.setText("Power Station On");
					cFrame.st.expectedWeight = 0;
					cFrame.st.productCart = new HashMap<>();
					cFrame.st.totalToPay = new BigDecimal(0.0);
					cFrame.st.paymentTotal = new BigDecimal(0.0);
					cFrame.mainScreen.displayProductCart();
				} else {
					cFrame.cardLayout.show(cFrame.getContentPane(), "startScreen");
					power.setText("Power Station Off");
				}
			});

			unblockStation.addActionListener(e -> {
				CustomerFrame cFrame = attendantFrame.cFrames.get(stationIndex);
				if (cFrame.isBeingUsed)
					cFrame.cardLayout.show(cFrame.getContentPane(), "mainScreen");
				else
					cFrame.cardLayout.show(cFrame.getContentPane(), "startScreen");
				blockStation.setText("Block");
			});

			blockStation.addActionListener(e -> {
				CustomerFrame cFrame = attendantFrame.cFrames.get(stationIndex);
				cFrame.cardLayout.show(cFrame.getContentPane(), "blockedScreen");
			});

			viewCart.addActionListener(e -> {
				attendantFrame.carts.get(index).displayProductCart();
				AttendantMainMenu.this.attendantFrame.cardLayout.show(attendantFrame.getContentPane(),
						"cart" + Integer.toString(stationIndex));
			});

			refillPaper.addActionListener(e -> {
				int delta = ReceiptPrinter.MAXIMUM_PAPER - st.linesOfPaperRemaining;
				try {
					AttendantMainMenu.this.attendantFrame.ac.addPaper(st, delta);
				} catch (OverloadException err) {
					err.printStackTrace();
				}
			});

			refillInk.addActionListener(e -> {
				int delta = ReceiptPrinter.MAXIMUM_INK - st.charactersOfInkRemaining;
				try {
					AttendantMainMenu.this.attendantFrame.ac.addInkCartridge(st, delta);
				} catch (OverloadException err) {
					err.printStackTrace();
				}
			});

			refillCoinsButton.addActionListener(e -> {
				BigDecimal value;
				switch (refillBanknotes.getSelectedIndex()) {
				case 0:
					value = new BigDecimal(0.05);
					break;
				case 1:
					value = new BigDecimal(0.10);
					break;
				case 2:
					value = new BigDecimal(0.25);
					break;
				case 3:
					value = new BigDecimal(1.0);
					break;
				default:
					value = new BigDecimal(2.0);
				}

				int delta = st.scs.coinDispensers.get(value).getCapacity() - st.scs.coinDispensers.get(value).size();
				for (int i = 0; i < delta; i++) {
					try {
						AttendantMainMenu.this.attendantFrame.ac.refillCoinDispenser(st,
								new Coin(Currency.getInstance("CAD"), value));
					} catch (OverloadException err) {
						err.printStackTrace();
					}
				}
			});

			refillBanknotesButton.addActionListener(e -> {
				int value;
				switch (refillBanknotes.getSelectedIndex()) {
				case 0:
					value = 5;
					break;
				case 1:
					value = 10;
					break;
				case 2:
					value = 20;
					break;
				default:
					value = 50;
				}

				int delta = st.scs.banknoteDispensers.get(value).getCapacity()
						- st.scs.banknoteDispensers.get(value).size();
				for (int i = 0; i < delta; i++) {
					try {
						AttendantMainMenu.this.attendantFrame.ac.refillBanknoteDispenser(st,
								new Banknote(Currency.getInstance("CAD"), value));
					} catch (OverloadException err) {
						err.printStackTrace();
					}
				}
			});

			emptyCoins.addActionListener(e -> {
				AttendantMainMenu.this.attendantFrame.ac.emptyCoinStorageUnit(st);
			});

			emptyBanknotes.addActionListener(e -> {
				AttendantMainMenu.this.attendantFrame.ac.emptyBanknoteStorageUnit(st);
			});
		}
	}
}
