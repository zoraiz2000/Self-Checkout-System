package org.gB.selfcheckout.test;

import org.gB.selfcheckout.software.ScanMembershipCard;
import org.gB.selfcheckout.software.State;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card.CardData;

public class TestScanMembershipCard {
	private State state;

	public class cDataStub implements CardData {
		public String number;

		public cDataStub(String number) {
			this.number = number;
		}

		@Override
		public String getType() {
			return null;
		}

		@Override
		public String getNumber() {
			return number;
		}

		@Override
		public String getCardholder() {
			return null;
		}

		@Override
		public String getCVV() {
			return null;
		}

	}

	@Before
	public void setup() {
		state = new State();
		state.scanMembershipCard = new ScanMembershipCard(state);
	}

	@Test
	public void testDisable() {
		cDataStub tester = new cDataStub("1234");
		state.scanMembershipCard.disabled(null);
		state.scanMembershipCard.cardDataRead(null, tester);
		Assert.assertNull(state.memberNumber);
	}

	@Test
	public void testEnable() {
		cDataStub tester = new cDataStub("1234");
		state.scanMembershipCard.enabled(null);
		state.scanMembershipCard.cardDataRead(null, tester);
		Assert.assertEquals(state.memberNumber, "1234");
	}
}
