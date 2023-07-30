package org.gB.selfcheckout.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.ReturnChange;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;

public class TestReturnChange {
	private State state;
	private BigDecimal[] coinDenom;
	private Banknote b0;
	private Banknote b1;
	private Banknote b2;
	private Banknote b3;

	// test coin tray not empty
	@Before
	public void setup() throws Exception {
		state = Main.init(2000, 1);
		state.enablePayment();
		this.coinDenom = state.scs.coinDenominations.toArray(new BigDecimal[0]);

		b0 = new Banknote(Currency.getInstance("CAD"), state.scs.banknoteDenominations[0]);
		b1 = new Banknote(Currency.getInstance("CAD"), state.scs.banknoteDenominations[1]);
		b2 = new Banknote(Currency.getInstance("CAD"), state.scs.banknoteDenominations[2]);
		b3 = new Banknote(Currency.getInstance("CAD"), state.scs.banknoteDenominations[3]);

	}

	public void initBanknote1() throws SimulationException, OverloadException {
		// don't have enough money inside banknote dispenser

		state.scs.banknoteDispensers.get(state.scs.banknoteDenominations[0]).load(b0, b0, b0, b0, b0);
		state.scs.banknoteDispensers.get(state.scs.banknoteDenominations[1]).load(b1, b1, b1, b1, b1);
		state.scs.banknoteDispensers.get(state.scs.banknoteDenominations[2]).load(b2, b2, b2, b2, b2);
		state.scs.banknoteDispensers.get(state.scs.banknoteDenominations[3]).load(b3, b3, b3, b3, b3);
	}

	public void initCoins1() throws SimulationException, OverloadException, DisabledException {
		// input 30 coins here
		for (int i = 0; i < 20; ++i) {
			state.scs.coinSlot.accept(new Coin(coinDenom[0]));
			state.scs.coinSlot.accept(new Coin(coinDenom[1]));
			state.scs.coinSlot.accept(new Coin(coinDenom[2]));
			state.scs.coinSlot.accept(new Coin(coinDenom[3]));
			state.scs.coinSlot.accept(new Coin(coinDenom[4]));
		}
	}

	public Banknote removeDanglingBanknoteWrapper(BanknoteSlot slot) {
		try {
			Banknote[] t = slot.removeDanglingBanknotes();
			// if have some banknote, then we return the 1st one
			if (t.length > 0)
				return t[0];
			// else return null
			return null;
		}
		// if exception occurs, return null
		catch (Exception e) {
			return null;
		}
	}

	// empty banknote will throw empty exception
	@Test(expected = EmptyException.class)
	public void testEmptyBanknoteDispenser() throws OverloadException, EmptyException, DisabledException {
		// don't put any money inside banknote dispenser
		state.returnChange.returnChange(new BigDecimal(state.scs.banknoteDenominations[0]));
		state.scs.coinTray.collectCoins();
	}

	// empty coins will throw empty exception
	@Test(expected = EmptyException.class)
	public void testEmptyCoinDispenser() throws OverloadException, EmptyException, DisabledException {
		// don't put any money inside banknote dispenser
		state.returnChange.returnChange(new BigDecimal(coinDenom[0].toString()));
		state.scs.coinTray.collectCoins();
	}

	@Test
	public void testBanknoteOutput1() throws OverloadException, EmptyException, DisabledException {
		// don't have enough money inside banknote dispenser
		Banknote b1 = new Banknote(Currency.getInstance("CAD"), state.scs.banknoteDenominations[3]);
		state.scs.banknoteDispensers.get(state.scs.banknoteDenominations[3]).load(b1, b1);

		BigDecimal inputSum = new BigDecimal(state.scs.banknoteDenominations[3]).multiply(new BigDecimal(3));
		state.returnChange.returnChange(inputSum);
		// removes the banknote 2 times
		ArrayList<Banknote> banknotesRemoved = new ArrayList<Banknote>();
		banknotesRemoved.add(removeDanglingBanknoteWrapper(state.scs.banknoteOutput));
		// currently, we should see a empty exception stack trace printed
		banknotesRemoved.add(removeDanglingBanknoteWrapper(state.scs.banknoteOutput));
		// check if the sum is the same
		BigDecimal actualSum = new BigDecimal(0);
		for (Banknote b : banknotesRemoved) {
			actualSum = actualSum.add(new BigDecimal(b.getValue()));
		}

		List<Coin> coinsReturned = state.scs.coinTray.collectCoins();
		for (Coin coin : coinsReturned) {
			if (coin != null) {
				actualSum = actualSum.add(coin.getValue());
			}
		}

		// since we only removed it twice, we test if it sums up correctly
		// can do assert equal here since banknotes are integers
		Assert.assertEquals(new BigDecimal(state.scs.banknoteDenominations[3]).multiply(new BigDecimal(2)), actualSum);

		// the third time we try to remove dangling banknote, we don't have any more
		// banknotes in the dispenser
		// however, the empty exception is caught inside the banknoteRemoved, so if we
		// keep simulating it we will get a simulation exception
		// right now we just print the empty exception inside banknoteRemoved, but we
		// can do other things there in the later
		// but it will actually be impossible to remove a dangling banknote that wasn't
		// there in the first place ... so we will not test it here
		// state.scs.banknoteOutput.removeDanglingBanknote();
	}

	@Test
	public void testBanknoteOutput2() throws OverloadException, EmptyException, DisabledException {
		initBanknote1();
		BigDecimal expectSum = new BigDecimal(
				b1.getValue() + b2.getValue() * 2 + b0.getValue() * 3 + b3.getValue() * 2);
		state.returnChange.returnChange(expectSum);
		BigDecimal actualSum = new BigDecimal(0);
		while (true) {
			// since we don't know when to stop remove dangling banknotes, we keep taking it
			// until we can't take anything anymore
			Banknote note = removeDanglingBanknoteWrapper(state.scs.banknoteOutput);
			if (note == null)
				break;
			actualSum = actualSum.add(new BigDecimal(note.getValue()));
		}
		List<Coin> coinsReturned = state.scs.coinTray.collectCoins();
		for (Coin coin : coinsReturned) {
			if (coin != null) {
				actualSum = actualSum.add(coin.getValue());
			}
		}
		// can do assert equal here since banknotes are integers
		Assert.assertEquals(expectSum, actualSum);
	}

	@Test
	public void testCoinOutput1() throws OverloadException, EmptyException, DisabledException {
		state.scs.coinSlot.accept(new Coin(coinDenom[0]));
		state.scs.coinSlot.accept(new Coin(coinDenom[1]));
		state.scs.coinSlot.accept(new Coin(coinDenom[2]));

		// NOTE: we need to remove the coins in the tray before return the change, or
		// else
		// the hardware/JUnit non-deterministically have coins in it, even though
		// NOTHING has outputted
		/// *
		state.scs.coinTray.collectCoins();
		// */

		// we expect coinDenom[0]+coinDenom[1]
		BigDecimal expectedSum = coinDenom[0].add(coinDenom[1]);
		state.returnChange.returnChange(expectedSum);
		List<Coin> coinsReturned = state.scs.coinTray.collectCoins();
		BigDecimal actualSum = new BigDecimal(0);
		for (Coin coin : coinsReturned) {
			if (coin != null) {
				actualSum = actualSum.add(coin.getValue());
			}
		}
		Assert.assertTrue(ReturnChange.checkSmallerThanEpsilon(expectedSum.subtract(actualSum)));
	}

	public void testCoinOutput2() throws Exception {
		initCoins1();

		BigDecimal expectedSum = new BigDecimal(0);
		for (int i = 0; i < 20; ++i) {
			expectedSum = expectedSum.add(coinDenom[0]);
			state.scs.coinSlot.accept(new Coin(coinDenom[0]));
		}

		// NOTE: we need to remove the coins in the tray before return the change, or
		// else
		// the hardware/JUnit non-deterministically have coins in it, even though
		// NOTHING has outputted
		/// *
		state.scs.coinTray.collectCoins();
		// */

		state.returnChange.returnChange(expectedSum);

		List<Coin> coinsReturned = state.scs.coinTray.collectCoins();
		BigDecimal actualSum = new BigDecimal(0);
		for (Coin coin : coinsReturned) {
			if (coin != null) {
				actualSum = actualSum.add(coin.getValue());
			}
		}

		Assert.assertTrue(ReturnChange.checkSmallerThanEpsilon(expectedSum.subtract(actualSum)));
	}

	@Test
	public void testCoinOutput2Test() throws Exception {
		testCoinOutput2();
	}

	public void testBanknoteAndCoinOutput1() throws OverloadException, EmptyException, DisabledException {
		initBanknote1();
		initCoins1();

		BigDecimal expectedSum = new BigDecimal(37.23);

		// NOTE: we need to remove the coins in the tray before return the change, or
		// else
		// the hardware/JUnit non-deterministically have coins in it, even though
		// NOTHING has outputted
		/// *
		state.scs.coinTray.collectCoins();
		// */

		state.returnChange.returnChange(expectedSum);

		List<Coin> coinsReturned = state.scs.coinTray.collectCoins();
		BigDecimal actualSum = new BigDecimal(0);
		for (Coin coin : coinsReturned) {
			if (coin != null) {
				actualSum = actualSum.add(coin.getValue());
			}
		}

		while (true) {
			// since we don't know when to stop remove dangling banknotes, we keep taking it
			// until we can't take anything anymore
			Banknote note = removeDanglingBanknoteWrapper(state.scs.banknoteOutput);
			if (note == null)
				break;
			actualSum = actualSum.add(new BigDecimal(note.getValue()));
		}

		state.returnChange.returnChange(expectedSum);
		Assert.assertTrue(expectedSum.subtract(actualSum).abs().compareTo(coinDenom[0]) <= 0);
	}

	@Test
	public void testBanknoteAndCoinOutput1Test() throws OverloadException, EmptyException, DisabledException {
		testBanknoteAndCoinOutput1();
	}

	// we will just copy paste two test cases
	@Test
	public void testResetState() throws Exception {
		// do 1 transaction
		testCoinOutput2();
		// the state should be reset inside returnChange() function
		// do another one
		testBanknoteAndCoinOutput1();
	}
}
