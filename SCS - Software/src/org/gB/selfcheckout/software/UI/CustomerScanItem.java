package org.gB.selfcheckout.software.UI;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;

/* 
 * Backend integration:
 *  Hardware:
 *  	barcode scanner
 *  Software:
 *  	List of products in database
 */
public class CustomerScanItem extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static String[] itemOptions;

	public CustomerFrame customerFrame;
	private JButton backButton;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel bottomPanel;
	private JComboBox itemMenu = new JComboBox();
	private JButton scanButton;
	private HashMap<String, BarcodedProduct> indexMap = new HashMap();

	public CustomerScanItem(CustomerFrame customerFrame) {

		this.customerFrame = customerFrame;
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

		setUpBackButton();
		setUpItemOptions();

		this.bottomPanel.setLayout(new GridLayout(3, 1));
		JLabel scanLabel = new JLabel("Select an item to scan");
		scanLabel.setFont(new Font("serif", Font.PLAIN, 20));
		bottomPanel.add(scanLabel);

		bottomPanel.add(itemMenu);

		scanButton = new JButton("(SCAN)");
		scanButton.addActionListener(this);
		bottomPanel.add(scanButton);

	}

	public void setUpBackButton() {

		gbc.insets = new Insets(3, 3, 3, 3);

		this.setLayout(new GridBagLayout());

		// Set up back button at the top left
		backButton = new JButton("Back");
		backButton.addActionListener(this);

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

	private void setUpItemOptions() {
		// Add barcoded products to drop down menu
		ProductDatabases.BARCODED_PRODUCT_DATABASE
				.forEach((barcode, barcodedProduct) -> indexMap.put(barcodedProduct.getDescription(), barcodedProduct));
		indexMap.forEach((description, product) -> itemMenu.addItem(description));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == backButton) {
			// Go back to main customer menu
			customerFrame.cardLayout.show(this.customerFrame.getContentPane(), "mainScreen");
		} else if (e.getSource() == scanButton) {
			// Scan item
			BarcodedProduct bcp = indexMap.get(itemMenu.getSelectedItem());
			Item item = customerFrame.st.idb.getInstance().getItem(bcp.getBarcode());
			customerFrame.currentItem = new BarcodedItem(bcp.getBarcode(), item.getWeight());
			customerFrame.st.scs.mainScanner.scan(item);
			// Go to "place your item in bagging area" panel
			if (customerFrame.st.itemScanned)
				customerFrame.st.scannedItems.add(item);
			customerFrame.waitingToBag();
		}

	}

}
