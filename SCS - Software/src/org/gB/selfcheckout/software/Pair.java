package org.gB.selfcheckout.software;

/*
 * A class to store an ordered pair.
 */
public class Pair<X, Y> {
	public X first;
	public Y second;

	/**
	 * Constructs an ordered pair.
	 * 
	 * @param first  The first element in the pair.
	 * @param second The second element in the pair.
	 */
	public Pair(X first, Y second) {
		this.first = first;
		this.second = second;
	}
}
