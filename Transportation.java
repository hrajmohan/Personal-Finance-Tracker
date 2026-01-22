public class Transportation extends Transaction {
	private String typeOfTransport;

	public Transportation() {
		this(null, 0.0, null, null);
	}

	public Transportation(String name, double cost, String typeOfPayment, String typeOfTransport) {
		super(name, cost, typeOfPayment);
		this.typeOfTransport = typeOfTransport;
	}

	public String getTypeOfTransport() {
		return typeOfTransport;
	}

	public void setTypeOfTransport(String typeOfTransport) {
		this.typeOfTransport = typeOfTransport;
	}

	@Override
	public String toString() {
		return "Transportation\n" + super.toString() + "Transportation Type: " + this.typeOfTransport + "\n";
	}

	/**
	 * Format for saving to file
	 */
	public String toFileString() {
		return "Transportation\n" + getName() + "\n" + getCost() + "\n" + getTypeOfPayment() + "\n"
				+ this.typeOfTransport + "\n";
	}
}