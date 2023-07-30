package org.gB.selfcheckout.software;

import java.util.ArrayList;
import java.util.Set;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

public class AttendantControl {
	private ArrayList<State> scsList; // List of all the states.
	private SupervisionStation supervisionStation; // The Supervision Station.
	ArrayList<PLUCodedProduct> productList = new ArrayList<PLUCodedProduct>(); // List of all the products.

	/**
	 * Initializes the Attendant Control.
	 * 
	 * @param scsList The list of all the states.
	 */
	public AttendantControl(ArrayList<State> scsList) {
		this.scsList = scsList;
		supervisionStation = new SupervisionStation();
//		for (State state : this.scsList) {
//			supervisionStation.add(state.scs);
//		}
	}

	/**
	 * Logs out of the supervisor station.
	 * 
	 * @return If the logout was successful.
	 */
	public boolean logout() {
		for (State state : this.scsList) {
			supervisionStation.remove(state.scs);
		}
		return true;
	}

	/**
	 * Shuts down the station remotely. (aka, uninstalls the "software")
	 * 
	 * @param state The state to shut down.
	 * 
	 * @return If the shutdown was successful.
	 */
	public boolean shutdownStation(State state) {
		for (BanknoteDispenser dispenser : state.scs.banknoteDispensers.values()) {
			dispenser.detachAll();
		}
		state.scs.banknoteInput.detachAll();
		state.scs.banknoteOutput.detachAll();
		state.scs.banknoteStorage.detachAll();
		state.scs.banknoteValidator.detachAll();
		state.scs.mainScanner.detachAll();
		state.scs.handheldScanner.detachAll();
		state.scs.cardReader.detachAll();
		for (CoinDispenser coinDispenser : state.scs.coinDispensers.values()) {
			coinDispenser.detachAll();
		}
		state.scs.coinSlot.detachAll();
		state.scs.coinStorage.detachAll();
		state.scs.coinTray.detachAll();
		state.scs.coinValidator.detachAll();
		state.scs.baggingArea.detachAll();
		state.scs.scanningArea.detachAll();
		state.scs.printer.detachAll();
		state.scs.screen.detachAll();

		state.poweredOn = false;

		return true;
	}

	/**
	 * Powers on the station.
	 * 
	 * @param stationId        The station to power on.
	 * @param scaleMaxWeight   The maximum weight of the scale.
	 * @param scaleSensitivity The sensitivity of the scale.
	 * 
	 * @return If the power on was successful.
	 */
	public boolean startupStation(int stationId, int scaleMaxWeight, int scaleSensitivity) throws Exception {
		new Main();
		this.scsList.set(stationId, Main.init(scaleMaxWeight, scaleSensitivity));

		return true;
	}

	/**
	 * Returns the list of all the states that are controlled by this Attendant
	 * Control.
	 * 
	 * @return The list of all the states that are controlled by this Attendant
	 *         Control.
	 */
	public ArrayList<State> getSCSList() {
		return this.scsList;
	}

	/**
	 * Adds ink to the given station.
	 * 
	 * @param state              The state to add the ink to.
	 * @param inkCartridgeAmount The amount of ink to add.
	 * 
	 * @return If the ink was successfully added.
	 * 
	 * @throws OverloadException
	 */
	public boolean addInkCartridge(State state, int inkCartridgeAmount) throws OverloadException {
		state.charactersOfInkRemaining += inkCartridgeAmount;
		state.scs.printer.addInk(inkCartridgeAmount);
		return true;
	}

	/**
	 * Adds paper to the given station.
	 * 
	 * @param state       The state to add the paper to.
	 * @param paperAmount The amount of paper to add.
	 * 
	 * @return If the paper was successfully added.
	 * 
	 * @throws OverloadException
	 */
	public boolean addPaper(State state, int paperAmount) throws OverloadException {
		state.linesOfPaperRemaining += paperAmount;
		state.scs.printer.addPaper(paperAmount);
		return true;
	}

	/**
	 * Loads the given coins into the given station.
	 * 
	 * @param state     The station to load the coin into.
	 * @param banknotes The coins to load.
	 * 
	 * @return If the coin dispenser unit was successfully refilled.
	 * 
	 * @throws OverloadException
	 */
	public boolean refillCoinDispenser(State state, Coin... coins) throws OverloadException {
		if (!state.poweredOn) {
			for (Coin coin : coins) {
				state.scs.coinDispensers.get(coin.getValue()).load(coin);
			}
			return true;
		}
		return false;
	}

	/**
	 * Empties the coin storage unit.
	 * 
	 * @param state The station to empty the coin storage unit.
	 * 
	 * @return If the coin storage unit was successfully emptied.
	 */
	public boolean emptyCoinStorageUnit(State state) {
		if (!state.poweredOn) {
			state.scs.coinStorage.unload();
			return true;
		}
		return false;
	}

	/**
	 * Loads the given banknotes into the given station.
	 * 
	 * @param state     The station to load the banknotes into.
	 * @param banknotes The banknotes to load.
	 * 
	 * @return If the banknote dispenser unit was successfully refilled.
	 * 
	 * @throws OverloadException
	 */
	public boolean refillBanknoteDispenser(State state, Banknote... banknotes) throws OverloadException {
		if (!state.poweredOn) {
			for (Banknote banknote : banknotes) {
				state.scs.banknoteDispensers.get(banknote.getValue()).load(banknote);
			}
			return true;
		}
		return false;
	}

	/**
	 * Empties the banknote storage unit.
	 * 
	 * @param state The station to empty the banknote storage unit.
	 * 
	 * @return If the banknote storage unit was successfully emptied.
	 */
	public boolean emptyBanknoteStorageUnit(State state) {
		if (!state.poweredOn) {
			state.scs.banknoteStorage.unload();
			return true;
		}
		return false;
	}

	/**
	 * Searches for a product in the product list, using the given product code.
	 * 
	 * @param partialLookUpCode The partial product code to search for.
	 * 
	 * @return An ArrayList of all the products that match the given product code.
	 */
	public ArrayList<PLUCodedProduct> looksUpProduct(String partialLookUpCode) {
		productList.clear();

		boolean track = true;
		char[] charArray = partialLookUpCode.toCharArray();
		Set<PriceLookupCode> keys = ProductDatabases.PLU_PRODUCT_DATABASE.keySet();
		for (PriceLookupCode key : keys) {
			for (int i = 0; i < key.toString().length(); i++) {
				if (i < charArray.length) {
					if (key.toString().charAt(i) != charArray[i]) {
						track = false;
					}
				} else {
					break;
				}
			}

			if (track) {
				productList.add(ProductDatabases.PLU_PRODUCT_DATABASE.get(key));
			}
		}

		return productList;
	}

	/**
	 * Blocks the station from being accessed by the customer.
	 * 
	 * @param state The station to block.
	 * 
	 * @return If the station was successfully blocked.
	 * 
	 * @throws DisabledException
	 * @throws OverloadException
	 */
	public boolean blockStation(State state) throws DisabledException, OverloadException {
		state.enableScanning();
		return true;
	}

	/**
	 * Removes a product from the given station.
	 * 
	 * @param state   The state to remove the product from.
	 * @param product The product to remove.
	 * 
	 * @return If the product was successfully removed.
	 */
	public boolean removeProduct(State state, Product product) {
		state.removeProduct(product);
		return true;
	}

	/**
	 * Approves a weight difference at the station.
	 * 
	 * @param state The state to approve the weight difference at.
	 * 
	 * @return If the weight difference was approved.
	 * 
	 * @throws OverloadException
	 */
	public boolean approveWeightDifference(State state) throws OverloadException {
		state.expectedWeight = state.scs.baggingArea.getCurrentWeight();
		state.enableScanning();
		return true;
	}
}