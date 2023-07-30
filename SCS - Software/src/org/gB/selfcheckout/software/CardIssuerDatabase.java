package org.gB.selfcheckout.software;

import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.external.CardIssuer;

/**
 * Simple database for relating card type with a cardIssuer
 *
 */
public class CardIssuerDatabase {
	// the string is the card's type
	// for example: VISA, MASTERCARD, DEBIT
	// if it's a gift card, maybe just append the card type after the word "GIFT"?
	// for example: GIFT_DEBIT
	// doesn't matter really, depends how it is used
	//
	private final Map<String, CardIssuer> cardToCardIssuerMap = new HashMap<String, CardIssuer>();
	public static CardIssuerDatabase c = new CardIssuerDatabase();
	// similar style to ItemDatabase

	/**
	 * Adds an entry to the database.
	 *
	 * @param card       Card to be added to the database.
	 * @param cardIssuer CardIssuer corresponding to the item to be added.
	 * @return 1 on successful entry, 0 if card already added in the database.
	 */
	public int addEntry(String s, CardIssuer cardIssuer) {
		// check if contained already, don't add it
		if (cardToCardIssuerMap.containsKey(s)) {
			// System.out.println("in addENtry, contains key is " + s);
			return 0;
		}
		// otherwise, can add
		cardToCardIssuerMap.put(s, cardIssuer);
		return 1;
	}

	/**
	 * Removes an entry to the database.
	 *
	 * @param card Card to be removed to the database.
	 */
	public void removeEntry(String s) {
		cardToCardIssuerMap.remove(s);
	}

	/**
	 * @param card Card to search for.
	 * @return CardIssuer corresponding to the card or NULL if no item found.
	 */
	public CardIssuer getCardIssuer(String s) {
		// if have the card, get it
		if (cardToCardIssuerMap.containsKey(s)) {
			return cardToCardIssuerMap.get(s);
		}
		// otherwise card doesn't exist, return null
		return null;
	}
}
