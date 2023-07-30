package org.gB.selfcheckout.test;

import static org.junit.Assert.assertEquals;

import org.gB.selfcheckout.software.Pair;
import org.junit.Test;

public class TestPair {
	@Test
	public void testPair() {
		Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1, 2);
		assertEquals(Integer.valueOf(1), pair.first);
		assertEquals(Integer.valueOf(2), pair.second);
	}
}
