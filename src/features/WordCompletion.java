package features;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

// TrieNode class representing a node in the Trie data structure
class TrieNode {
    TrieNode[] children;
    boolean wordEnd;

    public TrieNode() {
        // Initialize an array for children nodes based on ASCII characters
        this.children = new TrieNode[128];
        this.wordEnd = false;
    }
}

// Trie class to implement Trie operations for word completion
class Trie {
    private TrieNode root;

    // Constructor to initialize the Trie with an empty root node
    public Trie() {
        this.root = new TrieNode();
    }

    // Method to insert a word into the Trie
    public void insertNode(String word) {
        TrieNode nod = root;
        for (char ch : word.toCharArray()) {
            int index = ch;
            if (nod.children[index] == null) {
                nod.children[index] = new TrieNode();
            }
            nod = nod.children[index];
        }
        nod.wordEnd = true;
    }

    // Method to get word suggestions for a given prefix
    public List<String> getSuggestions(String prefix) {
        TrieNode nod = findNode(prefix);
        List<String> suggestions = new ArrayList<>();
        if (nod != null) {
            getAllWords(nod, prefix, suggestions);
        }

        // Sort suggestions by similarity (edit distance)
        suggestions.sort(Comparator.comparingInt(suggestion -> calculateEditDistance(prefix, suggestion)));

        // Increment search frequency for each suggestion
//        for (int i = 0; i < suggestions.size(); i++) {
//            SearchFrequency.incrementSearchFrequency(suggestions.get(i));
//        }

        return suggestions;
    }

    // Helper method to find the node corresponding to a given prefix
    private TrieNode findNode(String prefix) {
        TrieNode nod = root;
        for (char ch : prefix.toCharArray()) {
            int index = ch;
            if (nod.children[index] == null) {
                return null;
            }
            nod = nod.children[index];
        }
        return nod;
    }

    // Helper method to retrieve all words from a given node
    private void getAllWords(TrieNode node, String currentPrefix, List<String> suggestions) {
        if (node.wordEnd) {
            String suggestionWithoutOrSimilar = removeOrSimilar(currentPrefix);
            suggestions.add(suggestionWithoutOrSimilar);
        }

        for (int i = 0; i < node.children.length; i++) {
            if (node.children[i] != null) {
                char ch = (char) i;
                getAllWords(node.children[i], currentPrefix + ch, suggestions);
            }
        }
    }

    // Helper method to remove "or similar" from a suggestion
    private String removeOrSimilar(String suggestion) {
        // Assuming "or similar" is always at the end of the suggestion
        return suggestion.replace(" or similar", "");
    }

    // Helper method to calculate the edit distance between two words
    private int calculateEditDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(word1.charAt(i - 1), word2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }

    // Helper method to calculate the cost of substituting one character with another
    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    // Helper method to find the minimum of a set of numbers
    private int min(int... numbers) {
        return java.util.Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
}

// WordCompletion class to provide word completion functionality using a Trie
public class WordCompletion {
    // Static instance of the Trie for word completion
    public static Trie trie;

    // Main method to execute word completion
    public static void main(String[] args) {
        trie = new Trie();
        Scanner scanner = new Scanner(System.in);

        // Initialize the Trie with words from a JSON file
        initializeDictionaryFromJsonFile("JsonData/filtered_car_deals.json");

        // Continuous loop to take user input for word completion
        while (true) {
            System.out.print("Enter a prefix (type 'exit' to quit): ");
            String prefix = scanner.nextLine();

            if (prefix.equals("exit")) {
                // Exit the loop if the user enters 'exit'
                break;
            }

            // Get suggestions for the given prefix
            List<String> suggestions = getSuggestions(prefix);

            // Display the suggestions, if any
            if (!suggestions.isEmpty()) {
                System.out.println("Suggestions:");
                for (String suggestion : suggestions) {
                    System.out.println(suggestion);
                }
            } else {
                System.out.println("No suggestions found.");
            }
        }

        // Close the scanner
        scanner.close();
    }

    // Method to initialize the Trie with words from a JSON file
    public static void initializeDictionaryFromJsonFile(String filename) {
        trie = new Trie();

        try {
            // Read the JSON file and insert each car name into the Trie
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonArray = (ArrayNode) objectMapper.readTree(new File(filename));
            for (int i = 0; i < jsonArray.size(); i++) {
                trie.insertNode(jsonArray.get(i).get("name").asText().toLowerCase());
            }
        } catch (IOException e) {
            // Handle IO exception
            e.printStackTrace();
        }
    }

    // Method to get suggestions for a given prefix
    public static List<String> getSuggestions(String prefix) {
        return trie.getSuggestions(prefix);
    }
}
