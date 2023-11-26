package features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SpellChecking {

	private static final int ALPHABET_SIZE = 26;
	private static TrieNode root = new TrieNode();

	private static class TrieNode {
		TrieNode[] children = new TrieNode[ALPHABET_SIZE];
		boolean isEndOfWord = false;
	}

	public static void insert(String word) {
		TrieNode node = root;
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (c < 'a' || c > 'z') {
				continue;
			}
			int index = c - 'a';
			if (node.children[index] == null) {
				node.children[index] = new TrieNode();
			}
			node = node.children[index];
		}
		node.isEndOfWord = true;
	}

	public static boolean search(String word) {
		TrieNode node = root;
		for (int i = 0; i < word.length(); i++) {
			int index = word.charAt(i) - 'a';
			if (node.children[index] == null) {
				return false;
			}
			node = node.children[index];
		}
		return node != null && node.isEndOfWord;
	}

	public static void initializeDictionary(String filePath) throws IOException {
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			for (String word : line.split("\\W+")) {
				if (!word.isEmpty()) {
					insert(word.toLowerCase());
				}
			}
		}
		reader.close();
	}

	public static boolean checkSpelling(String word) {
		return search(word.toLowerCase());
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Take the path of the file to initialize the dictionary
//		System.out.println("Enter the path of the file to initialize the dictionary:");
//		String filePath = scanner.nextLine();

		try {
			// Initialize the dictionary from the specified file
			initializeDictionary("JsonData/filtered_car_deals.json");

			// Take input for the word to check for spelling
			System.out.println("Enter a word to check for spelling:");
			String spelling = scanner.nextLine();

			// Check spelling and print the result
			boolean check = checkSpelling(spelling);
			System.out.println("Is the word spelled correctly? " + check);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			scanner.close();
		}
	}
}