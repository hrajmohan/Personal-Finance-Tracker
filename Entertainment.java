public class Entertainment extends Transaction {

	private boolean isSubscription;

	public Entertainment() {
		this(null, 0.0, null, false);
	}

	public Entertainment(String name, double cost, String typeOfPayment, boolean isSubscription) {
		super(name, cost, typeOfPayment);
		this.isSubscription = isSubscription;
	}

	public boolean getSubscription() {
		return isSubscription;
	}

	public void setSubscription(boolean isSubscription) {
		this.isSubscription = isSubscription;
	}

	@Override
	public String toString() {
		return "Entertainment\n" + super.toString() + "Subscription: " + this.isSubscription + "\n";
	}

	/**
	 * Format for saving to file
	 */
	public String toFileString() {
		return "Entertainment\n" + getName() + "\n" + getCost() + "\n" + getTypeOfPayment() + "\n"
				+ this.isSubscription + "\n";
	}
}