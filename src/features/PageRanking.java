package features;

import webcrawling.AvisCanadaCrawl;
import webcrawling.BudgetCanadaCrawl;
import webcrawling.CarRentalWebCrawl;

import java.util.*;

public class PageRanking {
    private Map<String, Integer> pageScores;
    private PriorityQueue<Map.Entry<String, Integer>> priorityQueue;

    public PageRanking() {
        pageScores = new HashMap<>();
        // Initialize priority queue with a comparator for sorting
        priorityQueue = new PriorityQueue<>(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
    }

    public void calculatePageRank(Map<String, Integer> documentFrequencies) {
        if (documentFrequencies != null) {
            // Simple ranking based on frequency (replace with more advanced ranking)
            pageScores.putAll(documentFrequencies);

            // Populate the priority queue for ranking
            priorityQueue.addAll(pageScores.entrySet());
        } else {
            System.out.println("Error: Document frequencies are null");
        }
    }

    public List<Map.Entry<String, Integer>> getRankedPages() {
        // Get and return the ranked pages
        List<Map.Entry<String, Integer>> rankedPages = new ArrayList<>();

        while (!priorityQueue.isEmpty()) {
            rankedPages.add(priorityQueue.poll());
        }

        return rankedPages;
    }

    public static void main(String[] args) {
        BTree bTree = InvertedIndexing.indexDocumentsInFolder(new String[]{"AvisFiles","BudgetFiles","OrbitzFiles"});

        Map<String, Integer> documentFrequencies = bTree.search("kia");

        // Create a PageRank object
        PageRanking pageRank = new PageRanking();

        // Calculate PageRank based on the document frequencies
        pageRank.calculatePageRank(documentFrequencies);

        // Get and display ranked pages
        List<Map.Entry<String, Integer>> rankedPages = pageRank.getRankedPages();
        for (Map.Entry<String, Integer> entry : rankedPages) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void showRanking(String keyword){
        BTree bTree = InvertedIndexing.indexDocumentsInFolder(new String[]{"AvisFiles/","BudgetFiles/","CarRentalFiles/"});

        Map<String, Integer> documentFrequencies = bTree.search(keyword);

        if (!documentFrequencies.containsKey("avis_deals.html")){
            documentFrequencies.put("avis_deals.html",0);
        }if (!documentFrequencies.containsKey("budget_deals.html")){
            documentFrequencies.put("budget_deals.html",0);
        }if (!documentFrequencies.containsKey("orbitz_deals.html")){
            documentFrequencies.put("orbitz_deals.html",0);
        }

        // Create a PageRank object
        PageRanking pageRank = new PageRanking();

        // Calculate PageRank based on the document frequencies
        pageRank.calculatePageRank(documentFrequencies);

        System.out.println("Ranking of website for the selected Car Model:\n");
        // Get and display ranked pages
        List<Map.Entry<String, Integer>> rankedPages = pageRank.getRankedPages();
        int count = 1;
        for (Map.Entry<String, Integer> entry : rankedPages) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
            String website = entry.getKey();
            if (entry.getKey().contains("avis")){
                website = AvisCanadaCrawl.avisUrl;
            } else if (entry.getKey().contains("budget")) {
                website = BudgetCanadaCrawl.budgetUrl;
            } else if (entry.getKey().contains("orbitz")) {
                website = CarRentalWebCrawl.orbitzUrl;
            }
            System.out.println(count+". "+website);
            count++;
        }
    }
}