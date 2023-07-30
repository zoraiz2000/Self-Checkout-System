package org.gB.selfcheckout.software;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteSlotObserver;

public class ReturnChange implements BanknoteSlotObserver {
	public static final String RETURN_CHANGE_EPSILON_STRING = "0.000000001";
	public static final BigDecimal RETURN_CHANGE_EPSILON = new BigDecimal(RETURN_CHANGE_EPSILON_STRING);

	private State state;
	// these are the demonimations from state.scs, as arrays
	private int[] bankDenom;
	private BigDecimal[] coinDenom;

	// this array keeps track of how many remaining banknotes we need to give for
	// each change
	// it is used for the banknote slot observer, where if we emit a banknote, we
	// need to wait for the user to take away the dangling banknote.
	private int[] banknoteChangeList;
	// the current index for the banknote we are trying to return as change to the
	// user
	// NOTE: we maintain an invariant for curBanknoteIndex, where everything to the
	// left of curBanknoteIndex are paid to the user (i.e. for all 0 <= i <=
	// curBanknoteIndex, banknoteChangeList[i]=0)
	private int curBanknoteIndex;

	public ReturnChange(State state) {
		this.state = state;
		this.bankDenom = state.scs.banknoteDenominations;
		// to convert the list to a square bracket BigDecimal[], according to the
		// standard library documentation, if the array does not fit in the inputted
		// array, it will allocate a new buffer and return that
		// also discussed in
		// https://stackoverflow.com/questions/9572795/convert-list-to-array-in-java
		this.coinDenom = state.scs.coinDenominations.toArray(new BigDecimal[0]);
		this.banknoteChangeList = new int[bankDenom.length];
		resetBanknoteChangeState();
	}

	public static boolean checkSmallerThanEpsilon(BigDecimal input) {
		return input.abs().compareTo(RETURN_CHANGE_EPSILON) <= 0;
	}

	/**
	 * Does a divides b with repeated subtracting because if we use the big decimal
	 * division, we need to specify a rounding, which produces non-deterministic
	 * results with floating point arithmetic
	 * 
	 * @param a
	 * @param b
	 * 
	 */
	private Pair<BigDecimal, BigDecimal> myDivide(BigDecimal a, BigDecimal b) {
		BigDecimal retVal = new BigDecimal(0);
		while (a.abs().compareTo(b) >= 0) {
			// otherwise subtract and keep going
			a = a.subtract(b);
			retVal = retVal.add(new BigDecimal(1));
		}
		return new Pair<BigDecimal, BigDecimal>(retVal, a);
	}

	/**
	 * Pure function that takes in the ammount to return, the bank and coin
	 * denominations it will return the ammonut of change floored to the smallest
	 * coin denomination It will return a pair of lists that corresponds to each
	 * denomination. First projection of the pair corresponds to the banknotes, and
	 * the second projection corresponds to the coins Precondition: ammOfChange is
	 * non-negative, bankDenom and coinDenom are sorted in ascending order
	 * 
	 * @param ammOfChange The ammount of change to be returned
	 * @param bankDenom   The bank note denominations
	 * @param coinDenom   The coin denominations
	 */
	private Pair<int[], int[]> getChangeDenomCounts(BigDecimal ammOfChange, int[] bankDenom, BigDecimal[] coinDenom) {
		int[] banknoteChangeList = new int[bankDenom.length];
		int[] coinChangeList = new int[coinDenom.length];
		// runs a greedy algorithm on the banknotes first, then the coins
		// copies the big decimal
		BigDecimal ammLeft = new BigDecimal(ammOfChange.toString());

		// iterate over the banknote denominations backwards, since in the Main.java, we
		// know the it is sorted in ascending order, so we just iterate backwards
		// since ammLeft is non-negative, when we divide, the result should be
		// non-negative
		for (int i = bankDenom.length - 1; i >= 0; i--) {
			// if we are essentially 0, we are done
			if (checkSmallerThanEpsilon(ammLeft))
				break;

			BigDecimal curDenom = new BigDecimal(bankDenom[i]);

			/// *
			Pair<BigDecimal, BigDecimal> pair = myDivide(ammLeft, curDenom);
			ammLeft = pair.second;
			// use this much ammount of demonimation
			banknoteChangeList[i] = pair.first.intValue();
			// */
		}
		// after we are done with the banknotes, we go to the coins
		for (int i = coinDenom.length - 1; i >= 0; i--) {
			// if we are essentially 0, we are done
			if (checkSmallerThanEpsilon(ammLeft))
				break;

			// System.out.println("ammLeft in coin loop = "+ammLeft);
			BigDecimal curDenom = new BigDecimal(coinDenom[i].toString());

			/// *
			Pair<BigDecimal, BigDecimal> pair = myDivide(ammLeft, curDenom);
			ammLeft = pair.second;
			// use this much ammount of demonimation
			coinChangeList[i] = pair.first.intValue();
			// */
		}
		return new Pair<int[], int[]>(banknoteChangeList, coinChangeList);
	}

	/**
	 * Returns the coins specified in the input coinChangeList Precondition:
	 * elements in coinChangeList are non-negative
	 * 
	 * @param coinChangeList The number of coins to return that corresponds to each
	 *                       index of the coin denomination in the member variable
	 *                       coinDenom
	 * @throws OverloadException
	 * @throws EmptyException
	 * @throws DisabledException These exceptions are propagated up from the coin
	 *                           dispenser emit() function
	 */
	private void returnCoins(int[] coinChangeList) throws OverloadException, EmptyException, DisabledException {
		for (int i = 0; i < coinDenom.length; ++i) {
			// emit the number of coins in the coinChangeList
			for (int j = 0; j < coinChangeList[i]; ++j) {
				state.scs.coinDispensers.get(coinDenom[i]).emit();
			}
		}
	}

	/**
	 * Returns a banknote, if we still have remaining banknotes to return as change.
	 * Otherwise, this function does nothing The state of curBanknoteIndex is
	 * assumed to be in the invariant, described in the declaration of
	 * curBanknoteIndex
	 * 
	 * @throws OverloadException
	 * @throws EmptyException
	 * @throws DisabledException These exceptions are propagated up from the
	 *                           banknote dispenser emit() function
	 */
	private void returnABanknote() throws EmptyException, DisabledException, OverloadException {
		// if the current banknote change is used up (it is 0), go to the next index
		// with non-zero banknote change
		// note if everything in banknoteChangeList is 0, we will have
		// curBanknoteIndex=banknoteChangeList.length at the end
		while (curBanknoteIndex < banknoteChangeList.length && banknoteChangeList[curBanknoteIndex] == 0)
			++curBanknoteIndex;

		// if there are nothing to return for the banknote, we just do nothing
		if (curBanknoteIndex == banknoteChangeList.length)
			return;

		// otherwise, we can emit a banknote from the current banknote denomination
		state.scs.banknoteDispensers.get(bankDenom[curBanknoteIndex]).emit();
		// decrement the number of change
		--banknoteChangeList[curBanknoteIndex];
	}

	/**
	 * Returns the change with the given ammOfChange. Precondition: ammOfChange is
	 * non-negative
	 * 
	 * @param ammOfChange The ammount of change to be returned
	 * @throws OverloadException
	 * @throws EmptyException
	 * @throws DisabledException These exceptions are propagated up from
	 *                           returnCoins() and returnABanknote()
	 */
	public void returnChange(BigDecimal ammOfChange) throws OverloadException, EmptyException, DisabledException {
		BigDecimal change = new BigDecimal(ammOfChange.toString());

		// pair.first is the banknotesChanges, pair.second coinChanges
		Pair<int[], int[]> pair = getChangeDenomCounts(change, bankDenom, coinDenom);
		// return the coins
		returnCoins(pair.second);

		// reset the state just to be safe
		resetBanknoteChangeState();
		// set the bank notes change list
		banknoteChangeList = pair.first;

		// starts to return a banknote
		returnABanknote();
		// returnBanknotes(pair.first);
	}

	// this function can be used to reset the states
	public void resetBanknoteChangeState() {
		// 0 initialize the array
		for (int i = 0; i < banknoteChangeList.length; ++i)
			banknoteChangeList[i] = 0;
		// 0 initialize the curBanknoteIndex to 0
		curBanknoteIndex = 0;
	}

	// when the banknote is removed, we can try to emit more banknotes
	@Override
	public void banknoteRemoved(BanknoteSlot slot) {
		try {
			returnABanknote();
		}
		// we can catch 3 types of exceptions here by printing the stack trace ...
		catch (OverloadException | EmptyException | DisabledException e) {
			Main.error("Most likely that no more banknotes are available with banknote denomination"
					+ bankDenom[curBanknoteIndex]);
		}
	}

	// the rest of the observer methods are currently not needed
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void banknoteInserted(BanknoteSlot slot) {
	}

	@Override
	public void banknotesEjected(BanknoteSlot slot) {
	}
}
