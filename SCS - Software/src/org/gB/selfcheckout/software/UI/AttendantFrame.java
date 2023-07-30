package org.gB.selfcheckout.software.UI;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.gB.selfcheckout.software.AttendantControl;
import org.gB.selfcheckout.software.LoginDB;
import org.gB.selfcheckout.software.State;

/**
 * JFrame to contain the UI used by attendants.
 */
public class AttendantFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	List<State> states;
	public List<CustomerFrame> cFrames;
	AttendantControl ac;
	public CardLayout cardLayout = new CardLayout();
	LoginScreen login;
	AttendantMainMenu main;
	ArrayList<AttendantCartScreen> carts = new ArrayList<AttendantCartScreen>();
	AttendantStationShutDown shutDown;
	AlertPage alert;

	public AttendantFrame(List<State> states, List<CustomerFrame> cFrames, AttendantControl ac) {
		super("Attendant Station");
		this.states = states;
		this.cFrames = cFrames;
		this.ac = ac;
		LoginDB ldb = new LoginDB();
		ldb.addUser("", "");
		login = new LoginScreen(this, ldb);
		main = new AttendantMainMenu(this, states);
		alert = new AlertPage(this);
		shutDown = new AttendantStationShutDown(this);
		int i = 0;
		for (State state : states) {
			i++;
			carts.add(new AttendantCartScreen(this, state, i));
		}

		addPanels();

		cardLayout.show(this.getContentPane(), "login");
		this.setLayout(cardLayout);
		this.setSize(1280, 720);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private void addPanels() {

		this.getContentPane().setLayout(cardLayout);

		getContentPane().add(login, "login");
		getContentPane().add(main, "main");
		for (int i = 0; i < carts.size(); i++)
			getContentPane().add(carts.get(i), "cart" + Integer.toString(i));
		getContentPane().add(shutDown, "shutDown");
		getContentPane().add(alert, "alert");
	}
}
