public class Food extends Transaction {
	private double tips;
	private String storeName;

	public Food() {
		this(null, 0.0, null, 0.0, null);
	}

	public Food(String name, double cost, String typeOfPayment, double tips, String storeName) {
		super(name, cost, typeOfPayment);
		this.tips = tips;
		this.storeName = storeName;
	}

	/**
	 * Returns the amount spent from tips
	 * 
	 * @return the amount spent from tips
	 */
	public double getTips() {
		return tips;
	}

	/**
	 * Returns the name of restaurant or grocery store
	 * 
	 * @return the name of restaurant or grocery store
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * Update the amount spent on tips with a new value
	 * 
	 * @param tips amount spent on tips
	 */
	public void setTips(double tips) {
		this.tips = tips;
	}

	/**
	 * Update name to a restaurant or grocery store name with a new value
	 * 
	 * @param storeName the name of the restaurant or grocery store
	 */
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	/**
	 * To String method for food
	 * Returns name, price, type of payment, tips, and store name
	 */
	@Override
	public String toString() {
		return "Food\n" + super.toString() + "Tips: $" + getTips() + "\nStore Name: " + getStoreName() + "\n";
	}

	/**
	 * Format for saving to file
	 */
	public String toFileString() {
		return "Food\n" + getName() + "\n" + getCost() + "\n" + getTypeOfPayment() + "\n" + getTips() + "\n"
				+ getStoreName() + "\n";
	}
}