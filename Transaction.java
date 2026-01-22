public abstract class Transaction implements Comparable<Transaction> {
	private String typeOfPayment;
	private String name;
	private double cost;

	public Transaction() {
		this(null, 0.0, null);
	}

	public Transaction(String name, double cost, String typeOfPayment) {
		this.name = name;
		this.cost = cost;
		this.typeOfPayment = typeOfPayment;
	}

	public double getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public String getTypeOfPayment() {
		return typeOfPayment;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTypeOfPayment(String typeOfPayment) {
		this.typeOfPayment = typeOfPayment;
	}

	public int compareCost(Transaction t) {
		return Double.compare(this.cost, t.getCost());
	}

	public int compareTypeOfPayment(Transaction t) {
		if (t.getTypeOfPayment().equals(getTypeOfPayment())) {
			return 0;
		} else {
			return this.getTypeOfPayment().compareTo(t.getTypeOfPayment());
		}
	}

	@Override
	public int compareTo(Transaction t) {
		// Compare by: typeOfPayment, then cost, then name
		if (t.equals(this)) {
			return 0;
		} else if (!(t.getTypeOfPayment().equals(getTypeOfPayment()))) {
			return this.getTypeOfPayment().compareTo(t.getTypeOfPayment());
		} else if (!(t.getCost() == getCost())) {
			return Double.compare(this.getCost(), t.getCost());
		} else {
			return this.getName().compareTo(t.getName());
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Transaction)) {
			return false;
		} else {
			Transaction t = (Transaction) o;
			return getCost() == t.getCost() && getName().equals(t.getName())
					&& getTypeOfPayment().equals(t.getTypeOfPayment());
		}
	}

	@Override
	public String toString() {
		return "Name: " + name + "\nCost: $" + cost + "\nPaid by: " + typeOfPayment + "\n";
	}
	
	/**
	 * Format for saving to file - to be overridden by subclasses
	 */
	public String toFileString() {
		return toString();
	}
}