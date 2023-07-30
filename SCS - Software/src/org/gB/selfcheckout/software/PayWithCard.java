package org.gB.selfcheckout.software;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.external.CardIssuer;

public class PayWithCard implements CardReaderObserver {
	private State state;
	public BigDecimal amountToPay;
	public CardData cardData;
	public boolean isEnabled;

	public PayWithCard(State state, BigDecimal amountToPay) {
		this.state = state;
		this.amountToPay = amountToPay;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = true;
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = false;
	}

	@Override
	public void cardInserted(CardReader reader) {
		state.isCardInserted = true;
	}

	@Override
	public void cardRemoved(CardReader reader) {
		state.isCardInserted = false;
	}

	@Override
	public void cardTapped(CardReader reader) {
		// Makes no difference
	}

	@Override
	public void cardSwiped(CardReader reader) {
		// Makes no difference
	}

	@Override
	public void cardDataRead(CardReader reader, CardData data) {

		cardData = data;

		// If the amount to pay is greater than what is left to pay, only pay what's
		// left
		if (amountToPay.compareTo(state.totalToPay.subtract(state.paymentTotal)) == 1) {
			amountToPay = state.totalToPay.subtract(state.paymentTotal);
		}

		// Check that card data isn't corrupted?
		// Send error back to main & return if it is

		// try to actually take out the money from the card
		// we first need to query the card issuer from state
		CardIssuer issuer = state.cardIssuerDatabase.getCardIssuer(data.getType());
		if (issuer == null) {
			// if the issuer don't exist
			Main.error("card issuer is empty!");
			// System.out.println("card issuer is empty!");
			return;
		}

		System.out.println("not null");

		// otherwise, issuer is not null
		// quote from CardIssuer.java:
		// ``to debit a purchase, a hold is first placed on the amount and then the
		// transaction is posted. It does not quite work like that in the real world.``
		// we don't need to explicitly call releaseHold, since postTransaction should
		// run it
		int holdNum = issuer.authorizeHold(data.getNumber(), amountToPay);
		if (holdNum == -1) {
			Main.error("authorizeHold failed!");
		}

		System.out.println("authorizedm holdnum = " + holdNum);

		// if the transaction was successful
		if (!issuer.postTransaction(data.getNumber(), holdNum, amountToPay)) {
			// if the transaction failed
			Main.error("transaction failed!");
			// System.out.println("transaction failed!");
			return;
		}
		// else the transaction was successful!

		System.out.println("successful");

		// Update total
		state.paymentTotal = state.paymentTotal.add(amountToPay);
		System.out.println("total now: " + state.paymentTotal);

		// Check if this card has been used before
		int i = 0;
		for (Pair<CardData, BigDecimal> pair : state.cardPayments) {
			if (pair.first.getNumber().contentEquals(cardData.getNumber())) {
				// We have already recorded this card before
				// Update the payment total for that card here
				BigDecimal amountPaidByCard = new BigDecimal(0);
				amountPaidByCard = amountPaidByCard.add(amountToPay);
				amountPaidByCard = amountPaidByCard.add(pair.second);
				Pair<CardData, BigDecimal> updatedPair = new Pair<CardData, BigDecimal>(data, amountPaidByCard);
				state.cardPayments.set(i, updatedPair);
				return;
			}
			i++;
		}

		// Add a new card to the list
		state.cardPayments.add(new Pair<CardData, BigDecimal>(cardData, amountToPay));
		return;
	}

}
