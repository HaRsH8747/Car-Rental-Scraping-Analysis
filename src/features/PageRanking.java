package features;

import java.util.*;

class PageRank {
    private Map<String, Integer> pageScores = new HashMap<>();
    private PriorityQueue<Map.Entry<String, Integer>> priorityQueue;

    public PageRank() {
        // Initialize priority queue with a comparator for sorting
        priorityQueue = new PriorityQueue<>(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
    }

    public void calculatePageRank(Map<String, Integer> documentFrequencies) {
        // Simple ranking based on frequency (replace with more advanced ranking)
        pageScores.putAll(documentFrequencies);

        // Populate the priority queue for ranking
        priorityQueue.addAll(pageScores.entrySet());
    }

    public List<Map.Entry<String, Integer>> getRankedPages() {
        // Get and return the ranked pages
        List<Map.Entry<String, Integer>> rankedPages = new ArrayList<>();

        while (!priorityQueue.isEmpty()) {
            rankedPages.add(priorityQueue.poll());
        }

        return rankedPages;
    }
}

public class PageRanking {
    public static void main(String[] args) {

        BTree bTree = InvertedIndexing.indexDocumentsInFolder(new String[]{"AvisFiles","BudgetFiles","OrbitzFiles"});

        Map<String, Integer> documentFrequencies = bTree.search("ford");

        // Create a PageRank object
        PageRank pageRank = new PageRank();

        // Calculate PageRank based on the document frequencies
        pageRank.calculatePageRank(documentFrequencies);

        // Get and display ranked pages
        List<Map.Entry<String, Integer>> rankedPages = pageRank.getRankedPages();
        for (Map.Entry<String, Integer> entry : rankedPages) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void showRanking(String keyword){
        BTree bTree = InvertedIndexing.indexDocumentsInFolder(new String[]{"AvisFiles","BudgetFiles"});

        Map<String, Integer> documentFrequencies = bTree.search(keyword);

        // Create a PageRank object
        PageRank pageRank = new PageRank();

        // Calculate PageRank based on the document frequencies
        pageRank.calculatePageRank(documentFrequencies);

        // Get and display ranked pages
        List<Map.Entry<String, Integer>> rankedPages = pageRank.getRankedPages();
        for (Map.Entry<String, Integer> entry : rankedPages) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

