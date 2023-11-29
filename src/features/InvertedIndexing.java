package features;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class BTreeNode {
    List<String> keys;
    List<Map<String, Integer>> values;
    List<BTreeNode> children;

    BTreeNode() {
        keys = new ArrayList<>();
        values = new ArrayList<>();
        children = new ArrayList<>();
    }
}

class BTree {
    private int t;
    private BTreeNode root;

    BTree(int t) {
        this.t = t;
        this.root = new BTreeNode();
    }

    public void insert(String key, String document, int frequency) {
        BTreeNode root = this.root;
        if (root.keys.size() == (2 * t) - 1) {
            BTreeNode newRoot = new BTreeNode();
            this.root = newRoot;
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            insertNonFull(newRoot, key, document, frequency);
        } else {
            insertNonFull(root, key, document, frequency);
        }
    }

    private void insertNonFull(BTreeNode x, String key, String document, int frequency) {
        int i = x.keys.size() - 1;

        if (x.children.isEmpty()) {
            while (i >= 0 && key.compareTo(x.keys.get(i)) < 0) {
                i--;
            }
            i++;
            x.keys.add(i, key);
            x.values.add(i, new HashMap<>(Collections.singletonMap(document, frequency)));
        } else {
            while (i >= 0 && key.compareTo(x.keys.get(i)) < 0) {
                i--;
            }
            i++;

            if (x.children.get(i).keys.size() == (2 * t) - 1) {
                splitChild(x, i);
                if (key.compareTo(x.keys.get(i)) > 0) {
                    i++;
                }
            }

            insertNonFull(x.children.get(i), key, document, frequency);
        }
    }

    private void splitChild(BTreeNode x, int i) {
        BTreeNode y = x.children.get(i);
        BTreeNode z = new BTreeNode();
        x.children.add(i + 1, z);
        x.keys.add(i, y.keys.get(t - 1));
        x.values.add(i, y.values.get(t - 1));

        z.keys.addAll(y.keys.subList(t, y.keys.size()));
        z.values.addAll(y.values.subList(t, y.values.size()));
        y.keys.subList(t - 1, y.keys.size()).clear();
        y.values.subList(t - 1, y.values.size()).clear();

        if (!y.children.isEmpty()) {
            z.children.addAll(y.children.subList(t, y.children.size()));
            y.children.subList(t, y.children.size()).clear();
        }
    }

    public Map<String, Integer> search(String key) {
        return search(root, key);
    }

    private Map<String, Integer> search(BTreeNode x, String key) {
        int i = 0;
        while (i < x.keys.size() && key.compareTo(x.keys.get(i)) > 0) {
            i++;
        }

        if (i < x.keys.size() && key.equals(x.keys.get(i))) {
            return x.values.get(i);
        } else if (x.children.isEmpty()) {
            return null;
        } else {
            return search(x.children.get(i), key);
        }
    }
}

public class InvertedIndexing {
    public static void main(String[] args) {
        // Create a B-tree with a node size of 2
        BTree bTree = new BTree(2);

        // Indexing documents
        indexDocument(bTree, "document1.html", "This is an example document with the keyword example.");
        indexDocument(bTree, "document2.html", "Another document containing the keyword example multiple times.");

        // Search for a keyword
        String keyword = "example";
        Map<String, Integer> result = bTree.search(keyword);

        // Display search result
        if (result != null) {
            System.out.println("Occurrences of '" + keyword + "': " + result);
        } else {
            System.out.println("No occurrences of '" + keyword + "'.");
        }
    }

    private static String readHtmlFile(File file) {
//        StringBuilder content = new StringBuilder();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return content.toString();

        StringBuilder content = new StringBuilder();

        try {
            Document doc = Jsoup.parse(file, "UTF-8");
            content.append(doc.wholeText());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public static BTree indexDocumentsInFolder(String[] folderPaths) {
        BTree bTree = new BTree(2);
        for (String folderPath : folderPaths) {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            String documentName = file.getName();
                            String content = readHtmlFile(file);
                            indexDocument(bTree, documentName, content);
                        }
                    }
                }
            } else {
                System.out.println("Invalid folder path: " + folderPath);
            }
        }
        return bTree;
    }

    private static void indexDocument(BTree bTree, String documentName, String content) {
        // Tokenize the content (replace with your own tokenization logic)
        String[] tokens = content.split("\\s+");

        for (String token : tokens) {
            // Update the B-tree
            Map<String, Integer> frequencies = bTree.search(token.toLowerCase());
            if (frequencies == null) {
                frequencies = new HashMap<>();
            }

            int currentFrequency = frequencies.getOrDefault(documentName, 0);
            frequencies.put(documentName, currentFrequency + 1);
            bTree.insert(token.toLowerCase(), documentName, currentFrequency + 1);
        }
    }
}