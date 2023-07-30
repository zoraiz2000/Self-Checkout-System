package org.gB.selfcheckout.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import org.gB.selfcheckout.software.CardIssuerDatabase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.external.CardIssuer;

public class TestCardIssuerDatabase {

	private CardIssuerDatabase database;
	private static final String VISA_CARD_TYPE = "VISA";
	private static final String MASTERCARD_CARD_TYPE = "MASTERCARD";
	private static final String DEBIT_CARD_TYPE = "DEBIT";
	private static final String GIFT_DEBIT_CARD_TYPE = "GIFT_DEBIT";

	private CardIssuer visa_issuer;
	private CardIssuer masterCard_issuer;
	private CardIssuer debit_issuer;

	private static final BigDecimal DEFAULT_AMMOUNT = new BigDecimal(10000);
	// note that this test case will fail on April 30th, 2069
	private static final Calendar DEFAULT_EXP_DATE = getCalendar(30, 4, 2069);

	ArrayList<Card> cardList;

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
	public void setupTest() throws IOException {
		// Initialize database.
		database = new CardIssuerDatabase();

		// create issuers
		visa_issuer = new CardIssuer("VISA_ISSUER");
		masterCard_issuer = new CardIssuer("MASTERCARD_ISSUER");
		debit_issuer = new CardIssuer("DEBIT_ISSUER");

		// 6 cards
		cardList = new ArrayList<Card>();
		// some of these card names are copied from TestPayWithCard.java
		cardList.add(new Card(VISA_CARD_TYPE, "4520123412341234", "John Doe", "123", "9999", true, true));

		cardList.add(new Card(MASTERCARD_CARD_TYPE, "8976650412338276", "Jeff MacDonald", "344", "8923", true, true));

		cardList.add(new Card(DEBIT_CARD_TYPE, "7822123412349999", "John Doe", "842", "7790", true, true));
		cardList.add(new Card(DEBIT_CARD_TYPE, "9976650412338276", "Geoff Vooys", "344", "8923", true, true));
		cardList.add(new Card(GIFT_DEBIT_CARD_TYPE, "6666666666666666", "Chunyu Li", "123", "456", true, true));

		// OK: Java doesn't have macros, so the programmer needs to get cancer by copy
		// pasting the same thing super verbosely every time ... woohooo I love Java <3
		visa_issuer.addCardData(getTapCardData(cardList.get(0)).getNumber(),
				getTapCardData(cardList.get(0)).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(cardList.get(0)).getCVV(), DEFAULT_AMMOUNT);

		masterCard_issuer.addCardData(getTapCardData(cardList.get(1)).getNumber(),
				getTapCardData(cardList.get(1)).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(cardList.get(1)).getCVV(), DEFAULT_AMMOUNT);

		debit_issuer.addCardData(getTapCardData(cardList.get(2)).getNumber(),
				getTapCardData(cardList.get(2)).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(cardList.get(2)).getCVV(), DEFAULT_AMMOUNT);
		debit_issuer.addCardData(getTapCardData(cardList.get(3)).getNumber(),
				getTapCardData(cardList.get(3)).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(cardList.get(3)).getCVV(), DEFAULT_AMMOUNT);

		debit_issuer.addCardData(getTapCardData(cardList.get(4)).getNumber(),
				getTapCardData(cardList.get(4)).getCardholder(), DEFAULT_EXP_DATE,
				getTapCardData(cardList.get(4)).getCVV(), DEFAULT_AMMOUNT);

	}

	public void initDB() throws IOException {
		database.addEntry(cardList.get(0).tap().getType(), visa_issuer);
		database.addEntry(cardList.get(1).tap().getType(), masterCard_issuer);
		database.addEntry(cardList.get(2).tap().getType(), debit_issuer);
		database.addEntry(cardList.get(3).tap().getType(), debit_issuer);
		database.addEntry(cardList.get(4).tap().getType(), debit_issuer);
	}

	@Test
	public void testAddValidEntry() throws IOException {
		// should add successful
		Assert.assertEquals(database.addEntry(VISA_CARD_TYPE, visa_issuer), 1);
		Assert.assertEquals(database.getCardIssuer(VISA_CARD_TYPE), visa_issuer);
	}

	@Test
	public void testAddMultipleValidEntry() throws IOException {

		Assert.assertEquals(database.addEntry(VISA_CARD_TYPE, visa_issuer), 1);
		Assert.assertEquals(database.getCardIssuer(VISA_CARD_TYPE), visa_issuer);

		Assert.assertEquals(database.addEntry(MASTERCARD_CARD_TYPE, masterCard_issuer), 1);
		Assert.assertEquals(database.getCardIssuer(MASTERCARD_CARD_TYPE), masterCard_issuer);

		Assert.assertEquals(database.addEntry(DEBIT_CARD_TYPE, debit_issuer), 1);
		Assert.assertEquals(database.getCardIssuer(DEBIT_CARD_TYPE), debit_issuer);
	}

	@Test
	public void testDuplication() throws IOException {
		Assert.assertEquals(database.addEntry(VISA_CARD_TYPE, visa_issuer), 1);
		Assert.assertEquals(database.getCardIssuer(VISA_CARD_TYPE), visa_issuer);

		Assert.assertEquals(database.addEntry(VISA_CARD_TYPE, visa_issuer), 0);
	}

	@Test
	public void testSearchInvalidCardType() throws IOException {
		initDB();
		// the search shouldn't find the string, so that it returns null
		Assert.assertEquals(database.getCardIssuer("asdf"), null);
	}

	@Test
	public void testRemove() throws IOException {
		Assert.assertEquals(database.addEntry(VISA_CARD_TYPE, visa_issuer), 1);
		Assert.assertEquals(database.getCardIssuer(VISA_CARD_TYPE), visa_issuer);

		database.removeEntry(VISA_CARD_TYPE);
		// shouldn't be able to look it up now, so should return null
		Assert.assertEquals(database.getCardIssuer(VISA_CARD_TYPE), null);
	}
}
