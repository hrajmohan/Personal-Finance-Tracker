public class Home extends Transaction {
	private String category;

	public Home() {
		this(null, 0.0, null, null);
	}

	public Home(String name, double cost, String typeOfPayment, String category) {
		super(name, cost, typeOfPayment);
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Home\n" + super.toString() + "Home Expenses: " + this.category + "\n";
	}

	/**
	 * Format for saving to file
	 */
	public String toFileString() {
		return "Home\n" + getName() + "\n" + getCost() + "\n" + getTypeOfPayment() + "\n" + getCategory() + "\n";
	}
}