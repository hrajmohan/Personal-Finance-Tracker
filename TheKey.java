import java.util.Scanner;
import java.io.IOException;

public interface TheKey<T extends Comparable<T>> {

	/**
	 * Searches the transaction from the list
	 * 
	 * @param t the transaction to locate
	 * @return the location of transaction
	 */
	int binarySearch(T t);

	/**
	 * Sorts the transactions in ascending order
	 */
	void bubbleSort();

	/**
	 * Prints out all current transactions
	 */
	void printTransactions();

	/**
	 * Reads the file to populate the list of transactions
	 * 
	 * @param input the Scanner uses to read in the data
	 */
	void populateTransactions(Scanner input) throws IOException;
}