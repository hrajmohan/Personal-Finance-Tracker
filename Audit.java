import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.*;
import java.io.*;

public final class Audit implements TheKey<Transaction> {
	private ArrayList<Transaction> transactions = new ArrayList<Transaction>();

	public static void main(String[] args) {
		Audit a = new Audit();
		String month, year;

		Scanner userInput = new Scanner(System.in);

		System.out.println("Welcome to your finance tracker!");

		boolean flag = true;
		while (flag) {

			System.out.println("\nPlease select from one of the options: ");
			System.out.println("\n1. Open a previous file" + "\n2. Enter data for a new month" + "\n3. Exit");
			Scanner input = new Scanner(System.in);
			int answer = 0;
			try {
				answer = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter 1, 2, or 3.");
				continue;
			}

			if (answer == 1) {
				System.out.print("\nEnter the month (mm): ");
				month = input.nextLine();
				System.out.print("Enter the year (yyyy): ");
				year = input.nextLine();
				String fileName = month + "_" + year + ".txt";
				try {
					Scanner read = new Scanner(new File(fileName));
					a.transactions.clear(); // Clear previous data
					a.populateTransactions(read);
					a.printTransactions();
					read.close();
				} catch (Exception e) {
					System.out.println("A financial record for " + month + "/" + year + " does not exist.");
					System.out.println("Would you like to create a new file for this month and year?  (yes/no)");
					if (input.nextLine().equalsIgnoreCase("yes")) {
						answer = 2;
					} else {
						continue;
					}
				}
			}
			if (answer == 2) {
				System.out.print("\nEnter the month (mm): ");
				month = input.nextLine();
				System.out.print("Enter the year (yyyy): ");
				year = input.nextLine();
				String fileName = month + "_" + year + ".txt";
				File file = new File(fileName);
				try {
					boolean result = file.createNewFile();

					if (result) {
						a.newFile(file);
					} else {
						System.out.println("A financial record for " + month + "/" + year + " already exists.");
						System.out.println("Would you like to see the contents of this file?  (yes/no)");
						if (input.nextLine().equalsIgnoreCase("yes")) {
							Scanner read = new Scanner(file);
							a.transactions.clear();
							a.populateTransactions(read);
							a.printTransactions();
							read.close();
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			if (answer == 3) {
				System.out.println("\nGoodbye, come back again!");
				flag = false;
			}
		}
		userInput.close();
	}

	public double totalCost(ArrayList<Transaction> payment) {
		double cost = 0.0;
		for (Transaction t : payment) {
			cost += t.getCost();
		}
		return cost;
	}

	public double mean(ArrayList<Transaction> payment) {
		if (payment.size() == 0)
			return 0.0;
		return totalCost(payment) / numTransactions(payment);
	}

	public int numTransactions(ArrayList<Transaction> payment) {
		return payment.size();
	}

	@Override
	public int binarySearch(Transaction t) {
		bubbleSort();
		int low = 0;
		int high = transactions.size() - 1;
		while (low <= high) {
			int mid = (low + high) / 2;
			if (transactions.get(mid).equals(t)) {
				return mid;
			} else if (transactions.get(mid).compareTo(t) > 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		return -1;
	}

	/**
	 * Filters the transactions to one specific category
	 * 
	 * @param str String value of the category filter
	 * @return An Arraylist of transactions that fall under the param category.
	 */
	public ArrayList<Transaction> categFilter(String str) {
		ArrayList<Transaction> t = new ArrayList<Transaction>();
		if (str.equalsIgnoreCase("Home")) {
			for (int i = 0; i < transactions.size(); i++) { // Fixed: removed -1
				if (transactions.get(i) instanceof Home)
					t.add(transactions.get(i));
			}
		} else if (str.equalsIgnoreCase("Transportation")) {
			for (int i = 0; i < transactions.size(); i++) { // Fixed: removed -1
				if (transactions.get(i) instanceof Transportation)
					t.add(transactions.get(i));
			}
		} else if (str.equalsIgnoreCase("Entertainment")) {
			for (int i = 0; i < transactions.size(); i++) { // Fixed: removed -1
				if (transactions.get(i) instanceof Entertainment)
					t.add(transactions.get(i));
			}
		} else if (str.equalsIgnoreCase("Food")) {
			for (int i = 0; i < transactions.size(); i++) { // Fixed: removed -1
				if (transactions.get(i) instanceof Food)
					t.add(transactions.get(i));
			}
		}
		return t;
	}

	/**
	 * Sorts the transaction arraylist in this order: title, cost, typeOfPayment
	 */
	public void bubbleSort(ArrayList<Transaction> transactions) {
		for (int i = 0; i < transactions.size(); i++) {
			for (int j = 0; j < (transactions.size() - 1 - i); j++) {
				if (transactions.get(j).compareTo(transactions.get(j + 1)) > 0) {
					Transaction temp = transactions.get(j);
					transactions.set(j, transactions.get(j + 1));
					transactions.set(j + 1, temp);
				}
			}
		}
	}

	/**
	 * This method makes sure that transactions is sorted in this order:
	 * category, title, cost, typeOfPayment
	 */
	public void ultimateSort() {
		ArrayList<Transaction> h = categFilter("Home");
		ArrayList<Transaction> t = categFilter("Transportation");
		ArrayList<Transaction> e = categFilter("Entertainment");
		ArrayList<Transaction> f = categFilter("Food");
		
		transactions.clear();
		bubbleSort(h);
		bubbleSort(t);
		bubbleSort(e);
		bubbleSort(f);
		
		transactions.addAll(h);
		transactions.addAll(t);
		transactions.addAll(e);
		transactions.addAll(f);
	}

	/**
	 * Prints all transactions in the specified order.
	 */
	public void printTransactions() {
		bubbleSort();
		if (transactions.size() == 0) {
			System.out.println("No transactions to display.");
			return;
		}
		for (int i = 1; i <= transactions.size(); i++) {
			System.out.println("\n=== Transaction # " + i + " ===");
			System.out.print(transactions.get(i - 1).toString());
		}
		System.out.println("\nTotal transactions: " + transactions.size());
		System.out.println("Total cost: $" + String.format("%.2f", totalCost(transactions)));
	}

	public void populateTransactions(Scanner userInput) throws IOException {
		while (userInput.hasNextLine()) {
			String type = userInput.nextLine().trim();
			if (type.isEmpty())
				continue;

			if (type.equals("Food")) {
				String name = userInput.nextLine().trim();
				String cost = userInput.nextLine().trim();
				String typeOfPayment = userInput.nextLine().trim();
				String tips = userInput.nextLine().trim();
				String storeName = userInput.nextLine().trim();
				
				transactions.add(new Food(name, Double.parseDouble(cost), typeOfPayment, 
						Double.parseDouble(tips), storeName));
						
			} else if (type.equals("Transportation")) {
				String name = userInput.nextLine().trim();
				String cost = userInput.nextLine().trim();
				String typeOfPayment = userInput.nextLine().trim();
				String typeOfTransport = userInput.nextLine().trim();
				
				transactions.add(new Transportation(name, Double.parseDouble(cost), 
						typeOfPayment, typeOfTransport));
						
			} else if (type.equals("Entertainment")) {
				String name = userInput.nextLine().trim();
				String cost = userInput.nextLine().trim();
				String typeOfPayment = userInput.nextLine().trim();
				String isSubscription = userInput.nextLine().trim();
				
				transactions.add(new Entertainment(name, Double.parseDouble(cost), 
						typeOfPayment, Boolean.parseBoolean(isSubscription)));
						
			} else if (type.equals("Home")) {
				String name = userInput.nextLine().trim();
				String cost = userInput.nextLine().trim();
				String typeOfPayment = userInput.nextLine().trim();
				String category = userInput.nextLine().trim();
				
				transactions.add(new Home(name, Double.parseDouble(cost), 
						typeOfPayment, category));
			}
		}
	}

	public String suffix(int i) {
		int mod = i % 10;
		int mod100 = i % 100;
		
		if (mod100 >= 11 && mod100 <= 13) {
			return "th";
		}
		
		if (mod == 1) {
			return "st";
		} else if (mod == 2) {
			return "nd";
		} else if (mod == 3) {
			return "rd";
		}
		return "th";
	}

	public String categoryList() {
		Scanner c = new Scanner(System.in);
		boolean wrongUserInput = true;
		String choice = "";
		while (wrongUserInput) {
			System.out.println("\nWhich category does this transaction fall under?");
			System.out.println("Home\nTransportation\nFood\nEntertainment");
			System.out.print("\nEnter your choice: ");
			choice = c.nextLine();
			if (choice.equalsIgnoreCase("home") || choice.equalsIgnoreCase("transportation")
					|| choice.equalsIgnoreCase("food") || choice.equalsIgnoreCase("entertainment")) {
				wrongUserInput = false;
			} else {
				System.out.println("Invalid option. Please select one of the categories in the list.");
			}
		}
		return choice;
	}

	public void newFile(File file) {
		Transaction t = null;
		String category = "";
		double cost = 0;
		String title = "";
		PrintWriter w;
		boolean run = true;
		String typeOfPayment;
		Scanner scan = new Scanner(System.in);
		
		try {
			w = new PrintWriter(file);

			int count = 0;
			while (run) {
				count++;
				System.out.print("\nWhat is the name of the " + count + suffix(count) + " transaction: ");
				title = scan.nextLine();

				System.out.print("How much did this transaction cost? ");
				boolean validInput = false;
				while (!validInput) {
					try {
						cost = Double.parseDouble(scan.nextLine());
						validInput = true;
					} catch (NumberFormatException e) {
						System.out.print("Please enter a number in this format ##.##: ");
					}
				}

				System.out.print("Enter the type of payment: ");
				typeOfPayment = scan.nextLine();
				
				category = categoryList();

				if (category.equalsIgnoreCase("home")) {
					System.out.print("Enter category of payment (utility/personal/etc.): ");
					String type = scan.nextLine();
					t = new Home(title, cost, typeOfPayment, type);
					
				} else if (category.equalsIgnoreCase("transportation")) {
					System.out.print("Enter type of transportation: ");
					String typeOfTransport = scan.nextLine();
					t = new Transportation(title, cost, typeOfPayment, typeOfTransport);

				} else if (category.equalsIgnoreCase("entertainment")) {
					System.out.print("Is this a subscription? (true/false): ");
					String isSubscription = scan.nextLine();
					t = new Entertainment(title, cost, typeOfPayment, Boolean.parseBoolean(isSubscription));

				} else if (category.equalsIgnoreCase("food")) {
					System.out.print("Enter amount spent on tips: ");
					double tips = Double.parseDouble(scan.nextLine());
					System.out.print("Enter store name (grocery/restaurant): ");
					String storeName = scan.nextLine();
					t = new Food(title, cost, typeOfPayment, tips, storeName);
				}

				if (t != null) {
					transactions.add(t);
					w.write(t.toFileString());
				}

				System.out.print("\nWould you like to add more transactions? (yes/no): ");
				if (scan.nextLine().equalsIgnoreCase("no")) {
					run = false;
				}
			}
			w.close();
			System.out.println("\nFile saved successfully!");

		} catch (IOException e) {
			System.out.println("Error writing to file: " + e.getMessage());
		}
	}

	@Override
	public void bubbleSort() {
		bubbleSort(transactions);
	}
	
	// GUI helper methods
	public void addTransaction(Transaction t) {
		transactions.add(t);
	}
	
	public void clearTransactions() {
		transactions.clear();
	}
	
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	public void setCurrentFile(File file) {
		// For GUI compatibility
	}
}
