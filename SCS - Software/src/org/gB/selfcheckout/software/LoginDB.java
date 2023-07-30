package org.gB.selfcheckout.software;

import java.util.HashMap;

/**
 * Rudimentary Login Database
 * 
 * Handles the setting of new users, removal of users, and verifying logins
 * 
 **/
public final class LoginDB {
	private final HashMap<String, String> db = new HashMap<>();

	/**
	 * Adds a user to the database.
	 *
	 * @param username Username of the user
	 * @param password Password of the user
	 * @return 1 on successful entry, otherwise returns -1.
	 */
	public int addUser(String username, String password) {
		if (db.putIfAbsent(username, password) != null) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Removes a user from the database.
	 *
	 * @param username Username of the user
	 */
	public void removeUser(String username) {
		db.remove(username);
	}

	/**
	 * Adds a user to the database.
	 *
	 * @param username Username of the possible user
	 * @param password Password of the possible user
	 * @return True if the password matches the username, False otherwise
	 */
	public Boolean login(String username, String password) {
		return password.equals(db.get(username));
	}
}
