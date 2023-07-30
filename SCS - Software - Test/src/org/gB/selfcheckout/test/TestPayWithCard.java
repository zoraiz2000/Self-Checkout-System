package org.gB.selfcheckout.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import org.gB.selfcheckout.software.CardIssuerDatabase;
import org.gB.selfcheckout.software.Main;
import org.gB.selfcheckout.software.Pair;
import org.gB.selfcheckout.software.PayWithCard;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.external.CardIssuer;

public class TestPayWithCard {

	private State state;
	private CardReader cardReader;
	private Card validTestCard1;
	private Card validTestCard2;
	private Card validTestCard3;
	private PayWithCard payWithCard;
	private BigDecimal amountToPay;
	// the stuff needed for CardIssuerDatabas
	private CardIssuerDatabase database;
	private CardIssuer visa_issuer;
	private CardIssuer masterCard_issuer;
	private CardIssuer debit_issuer;
	private static final BigDecimal DEFAULT_AMMOUNT = new BigDecimal(10000);
	// note that this test case will fail on April 30th, 2069
	private static final Calendar DEFAULT_EXP_DATE = getCalendar(30, 4, 2069);

	// initialize a calendar with a helper function, since it is impossible to
	// construct a calendar with expected data in Java <3
	// https://stackoverflow.com/questions/8605393/initialize-a-calendar-in-a-constructor
	public static Calendar getCalendar(int day, int month, int year) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, day);
		return date;
	}

	public CardData getTapCardData(Card card) {
		CardData data;
		while (true) {
			try {
				data = card.tap();
				return data;
			} catch (Exception e) {
			}
		}
	}

	@Before
	public void setup() throws IOException {
		// Initialize State.
		try {
			state = Main.init(100, 1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Set variables to respective components of State.
		assert state != null;

		cardReader = state.scs.cardReader;

		// Create test credit cards
		validTestCard1 = new Card("VISA", "4520123412341234", "John Doe", "123", "9999", true, true);
		validTestCard2 = new Card("MASTERCARD", "8976650412338276", "Jeff MacDonald", "344", "8923", true, true);
		validTestCard3 = new Card("GIFT_DEBIT", "6666666666666666", "Jeff MacDonald", "344", "8923", true, true);

		// Initialize test class
		amountToPay = new BigDecimal(13.75);

		BigDecimal tTP = new BigDecimal(55.15);
		state.totalToPay = tTP;

		// we need to initialize the card issuer database here
		// Initialize database.
		database = new CardIssuerDatabase();

		// create issuers
		visa_issuer = new CardIssuer("VISA_ISSUER");
		masterCard_issuer = new CardIssuer("MASTERCARD_ISSUER");
		debit_issuer = new CardIssuer("DEBIT_ISSUER");

		database.addEntry("VISA", visa_issuer);
		database.addEntry("MASTERCARD", masterCard_issuer);
		database.addEntry("GIFT_DEBIT", debit_issuer);

		// add the cards to the issuers
		visa_issuer.addCardData(getTapCardData(validTestCard1).getNumber(),
				getTapCardData(validTestCard1).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(validTestCard1).getCVV(), DEFAULT_AMMOUNT);
		masterCard_issuer.addCardData(getTapCardData(validTestCard2).getNumber(),
				getTapCardData(validTestCard2).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(validTestCard2).getCVV(), DEFAULT_AMMOUNT);
		debit_issuer.addCardData(getTapCardData(validTestCard3).getNumber(),
				getTapCardData(validTestCard3).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(validTestCard3).getCVV(), DEFAULT_AMMOUNT);
		state.cardIssuerDatabase = database;
	}

	@Test
	public void testAddsCorrectValue() {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		while (true) {
			try {
				cardReader.swipe(validTestCard1);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		Assert.assertEquals(state.paymentTotal, amountToPay);
		state.cardPayments.clear();
		state.paymentTotal = new BigDecimal(0);
	}

	@Test
	public void testAmountToPayTooHigh() {
		BigDecimal highAmountToPay = new BigDecimal(60.25);
		payWithCard = new PayWithCard(state, highAmountToPay);
		cardReader.attach(payWithCard);
		while (true) {
			try {
				cardReader.swipe(validTestCard1);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		payWithCard.amountToPay = amountToPay;
		// Paid exactly the price of the products scanned, not more
		Assert.assertEquals(state.paymentTotal, state.totalToPay);
		state.cardPayments.clear();
		state.paymentTotal = new BigDecimal(0);
	}

	@Test
	public void testStoresSingleTapCardData() throws IOException {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		while (true) {
			try {
				cardReader.tap(validTestCard1);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		// Check that stored card is correct
		Pair<CardData, BigDecimal> storedCardPair = state.cardPayments.get(0);
		Assert.assertEquals(storedCardPair.first.getNumber(), "4520123412341234");
		Assert.assertEquals(storedCardPair.first.getCVV(), "123");
		Assert.assertEquals(storedCardPair.first.getCardholder(), "John Doe");
		Assert.assertEquals(storedCardPair.first.getType(), "VISA");
		Assert.assertEquals(storedCardPair.second, amountToPay);
		state.cardPayments.clear();
		state.paymentTotal = new BigDecimal(0);
	}

	@Test
	public void testStoresMultipleTapCardData() throws IOException {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		// Tap once
		while (true) {
			try {
				cardReader.tap(validTestCard1);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		// Tap twice
		while (true) {
			try {
				cardReader.tap(validTestCard1);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		Pair<CardData, BigDecimal> storedCardPair = state.cardPayments.get(0);
		Assert.assertEquals(state.cardPayments.size(), 1);
		Assert.assertEquals(storedCardPair.first.getNumber(), "4520123412341234");
		Assert.assertEquals(storedCardPair.first.getCVV(), "123");
		Assert.assertEquals(storedCardPair.first.getCardholder(), "John Doe");
		Assert.assertEquals(storedCardPair.first.getType(), "VISA");
		Assert.assertEquals(storedCardPair.second, amountToPay.add(amountToPay));
		state.cardPayments.clear();
		state.paymentTotal = new BigDecimal(0);
	}

	@Test
	public void testPayWithTwoCards() {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		// Tap once
		while (true) {
			try {
				cardReader.tap(validTestCard1);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		// Tap twice
		while (true) {
			try {
				cardReader.tap(validTestCard2);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		Assert.assertEquals(state.cardPayments.size(), 2);

		Pair<CardData, BigDecimal> storedCardPair1 = state.cardPayments.get(0);
		Assert.assertEquals(storedCardPair1.first.getNumber(), "4520123412341234");
		Assert.assertEquals(storedCardPair1.first.getCVV(), "123");
		Assert.assertEquals(storedCardPair1.first.getCardholder(), "John Doe");
		Assert.assertEquals(storedCardPair1.first.getType(), "VISA");
		Assert.assertEquals(storedCardPair1.second, amountToPay);

		Pair<CardData, BigDecimal> storedCardPair2 = state.cardPayments.get(1);
		Assert.assertEquals(storedCardPair2.first.getNumber(), "8976650412338276");
		Assert.assertEquals(storedCardPair2.first.getCVV(), "344");
		Assert.assertEquals(storedCardPair2.first.getCardholder(), "Jeff MacDonald");
		Assert.assertEquals(storedCardPair2.first.getType(), "MASTERCARD");
		Assert.assertEquals(storedCardPair2.second, amountToPay);

		state.cardPayments.clear();
		state.paymentTotal = new BigDecimal(0);
	}

	@Test
	public void testPayWithGiftCard() {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		// Tap once
		while (true) {
			try {
				cardReader.tap(validTestCard3);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		Assert.assertEquals(state.cardPayments.size(), 1);
		Pair<CardData, BigDecimal> storedCardPair1 = state.cardPayments.get(0);

		CardData giftCardData = getTapCardData(validTestCard3);

		// NOTE: .equals instance for CardData is NOT implemented in the hardware! This
		// is a bug!
		// Assert.assertTrue(storedCardPair1.first.equals(getTapCardData(validTestCard3)));
		Assert.assertEquals(storedCardPair1.first.getNumber(), giftCardData.getNumber());
		Assert.assertEquals(storedCardPair1.first.getCVV(), giftCardData.getCVV());
		Assert.assertEquals(storedCardPair1.first.getCardholder(), giftCardData.getCardholder());
		Assert.assertEquals(storedCardPair1.first.getType(), giftCardData.getType());
		Assert.assertEquals(storedCardPair1.second, amountToPay);
	}

	@Test
	public void testPayWithGiftCardNoEnoughMoney() {
		BigDecimal pay = DEFAULT_AMMOUNT.add(new BigDecimal(1));
		state.totalToPay = pay;
		// we shouldn't have enough money to pay it ...
		payWithCard = new PayWithCard(state, pay);
		cardReader.attach(payWithCard);
		// Tap once
		while (true) {
			try {
				cardReader.tap(validTestCard3);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		// shouldn't have added anything inside card payments
		Assert.assertEquals(state.cardPayments.size(), 0);
	}

	@Test
	public void testPayWithInvalidCardType() {
		// "ASDASD" is not a recognized card type
		Card invalidType = new Card("ASDASD", "1111111111111111", "aaa", "842", "7790", true, true);
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		// Tap once
		while (true) {
			try {
				cardReader.tap(invalidType);
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		// shouldn't have added anything inside card payments
		Assert.assertEquals(state.cardPayments.size(), 0);
	}

	@Test
	public void testEnabled() {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		payWithCard.enabled(cardReader);
		Assert.assertTrue(payWithCard.isEnabled);
	}

	@Test
	public void testDisabled() {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		payWithCard.enabled(cardReader);
		payWithCard.disabled(state.scs.cardReader);
		Assert.assertFalse(payWithCard.isEnabled);
	}

	@Test
	public void testCardInserted() {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		// Insert
		while (true) {
			try {
				cardReader.insert(validTestCard1, "9999");
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		Assert.assertTrue(state.isCardInserted);
		state.cardPayments.clear();
		state.paymentTotal = new BigDecimal(0);
	}

	@Test
	public void testCardRemoved() {
		payWithCard = new PayWithCard(state, amountToPay);
		cardReader.attach(payWithCard);
		// Insert
		while (true) {
			try {
				cardReader.insert(validTestCard1, "9999");
				break;
			} catch (IOException e) {
				// Keep trying
			}
		}
		// Remove
		while (true) {
			try {
				cardReader.remove();
				break;
			} catch (Exception e) {
				// Keep trying
			}
		}
		Assert.assertFalse(state.isCardInserted);
		state.cardPayments.clear();
		state.paymentTotal = new BigDecimal(0);
	}
}
