package org.gB.selfcheckout.software;

/**
 * Class to handle the use case where the user has finished adding items, and
 * they're ready to checkout. (aka. "Payment Mode")
 */
public class PaymentMode {
	private State state;
	private boolean paymentModeActive;
	// private PayWithCard payWithCard;

	/**
	 * Instantiates this use case handler.
	 * 
	 * @param state The instance of the system this.state to which the transaction
	 *              is to be finalized.
	 */
	public PaymentMode(State state) {
		this.state = state;
		this.paymentModeActive = false;
	}

	/**
	 * This method is called when the user indicates they are done adding items, and
	 * they're ready to checkout.
	 */
	public void enablePaymentMode() {
		if (!this.paymentModeActive) {
			// Enable the payment mode.
			state.enablePayment();

			// Ask the user for their payment method, and the amount they're paying.
			// This information comes from the comment block below.
			// this.payWithCard = new PayWithCard(state, state.paymentTotal);

			// Set the payment mode active flag.
			this.paymentModeActive = true;
		} else {
			Main.error("Attempting to enable payment mode, while it is already active.");
		}

		/**
		 * Ask the GUI to update its state into the payment mode. - Display the payment
		 * methods. - Once the user has selected a payment method, display the total
		 * amount to be paid and the amount already paid, finally we'll also allow the
		 * user to enter the amount they'd like to pay on that method. - Once the user
		 * has selected the payment method and the amount, we'll enable the payment
		 * method and call the handler. - If the user still needs to pay, we'll display
		 * the remaining amount to be paid and the amount already paid. (Basically, a
		 * while loop or something.)
		 * 
		 * - Payment methods will handle the change.
		 */
	}

	/**
	 * This method disables the payment method.
	 */
	public void disablePaymentMode() {
		if (this.paymentModeActive) {
			// Disable the payment mode.
			state.enableCheckout();

			// Reset the payWithCard instance.
			// this.payWithCard = null;

			// Set the payment mode active flag.
			this.paymentModeActive = false;
		} else {
			Main.error("Attempting to disable payment mode, while it is already disabled.");
		}
	}

	// #region Getters
	/**
	 * Returns the state of the payment mode.
	 * 
	 * @return boolean The state of the payment mode.
	 */
	public boolean isPaymentModeActive() {
		return this.paymentModeActive;
	}
	// #endregion Getters
}