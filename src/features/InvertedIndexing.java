package features;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class InvertedIndexing<Value> {
    private static int N; // size of B-tree
    private static int M = 5; // B-tree order
    private BTreeNode rt; // root node
    private static boolean is_exit;
    private static int maximum = 0;
    private static ArrayList<Hashtable<Integer, Integer>> arr = new ArrayList<>();
    private static int document_index;
    private static Hashtable<Integer, String> webpage_list = new Hashtable<>();

    // Defining the Structure of BTreeNode
    private class BTreeNode {
        private List<String> keys;
        private List<Value> values;
        private List<BTreeNode> children;

        BTreeNode() {
            this.keys = new ArrayList<>();
            this.values = new ArrayList<>();
            this.children = new ArrayList<>();
        }
    }

    public int size() {
        return N;
    }

    // Validating that String is there or not
    public boolean contains(String k) {
        return get(k) != null;
    }

    private Value get(String k) {
        if (k == null)
            throw new NullPointerException();

        if (k.length() == 0)
            throw new IllegalArgumentException("Word must have be greater than one letter");

        return get(rt, k);
    }

    private Value get(BTreeNode x, String k) {
        if (x == null)
            return null;

        int i = 0;
        while (i < x.keys.size() && k.compareTo(x.keys.get(i)) > 0) {
            i++;
        }

        if (i < x.keys.size() && k.equals(x.keys.get(i))) {
            return x.values.get(i);
        } else if (x.children.size() > i) {
            return get(x.children.get(i), k);
        } else {
            return null;
        }
    }

    // Inserting string and its value
    public int put(String c, Value value) {
        if (!contains(c)) {
            N++;
            rt = put(rt, c, value);
            return 0;
        } else
            return 1;
    }

    private BTreeNode put(BTreeNode x, String key, Value value) {
        if (x == null) {
            BTreeNode newNode = new BTreeNode();
            newNode.keys.add(key);
            newNode.values.add(value);
            return newNode;
        }

        int i = 0;
        while (i < x.keys.size() && key.compareTo(x.keys.get(i)) > 0) {
            i++;
        }

        if (i < x.keys.size() && key.equals(x.keys.get(i))) {
            x.values.set(i, value);
        } else if (x.children.size() > i) {
            BTreeNode child = x.children.get(i);
            BTreeNode newChild = put(child, key, value);
            if (newChild != child) {
                x.keys.add(i, newChild.keys.get(0));
                x.values.add(i, newChild.values.get(0));
                x.children.add(i + 1, newChild.children.get(0));
                if (x.keys.size() > M - 1) {
                    return split(x);
                }
            }
        } else {
            x.keys.add(i, key);
            x.values.add(i, value);
        }

        return x;
    }

    private BTreeNode split(BTreeNode x) {
        BTreeNode newNode = new BTreeNode();
        int middle = x.keys.size() / 2;

        newNode.keys.add(x.keys.remove(middle));
        newNode.values.add(x.values.remove(middle));

        List<String> rightKeys = new ArrayList<>(x.keys.subList(middle, x.keys.size()));
        x.keys.subList(middle, x.keys.size()).clear();
        newNode.keys.addAll(rightKeys);

        List<Value> rightValues = new ArrayList<>(x.values.subList(middle, x.values.size()));
        x.values.subList(middle, x.values.size()).clear();
        newNode.values.addAll(rightValues);

        List<BTreeNode> rightChildren = new ArrayList<>(x.children.subList(middle + 1, x.children.size()));
        x.children.subList(middle + 1, x.children.size()).clear();
        newNode.children.addAll(rightChildren);

        return newNode;
    }

    // Other methods for tokenizing, printing frequency, etc. remain the same...

    // Add your tokenizing, printing frequency, and indexing methods here...

    // Other methods for tokenizing, printing frequency, etc. remain the same...

    // Tokenize input documents and build the inverted index
    private void tokenizeAndBuildIndex(InvertedIndexing<Integer> st) {
        String folderList[] = {"AvisHtml", "BudgetHtml"};
        document_index = -1;

        for (String folder : folderList) {
            // Assuming each document is a separate file in the folder
            File webPageFolder = new File(folder);

            for (File input : webPageFolder.listFiles()) {
                String text = "";

                if (!input.isHidden()) {
                    try {
                        document_index++;
                        webpage_list.put(document_index, input.getName());
                        Document document = Jsoup.parse(input, "UTF-8");
                        text = text + document.body().text();
                    } catch (Exception e) {
                        continue;
                    }

                    text = text.toLowerCase();
                    String[] words = text.split(" ");

                    for (String word : words) {
                        if (st.get(word) == null) {
                            arr.add(new Hashtable<Integer, Integer>());
                            st.put(word, N);
                        }

                        int wordIndex = st.get(word);

                        try {
                            if (wordIndex <= arr.size()) {
                                int freq = arr.get(wordIndex).get(document_index);
                                arr.get(wordIndex).put(document_index, freq + 1);
                            }
                        } catch (Exception e) {
                            try {
                                arr.get(wordIndex).put(document_index, 1);
                            } catch (Exception e2) {
                                System.out.println(e2);
                            }
                        }
                    }

                    if (words.length > maximum) {
                        maximum = words.length;
                    }
                }
            }
        }
    }

    // Print frequency for a given word index
    public static void printFrequency(int wordIndex) {
        for (int i = 0; i <= document_index; i++) {
            String filename = webpage_list.get(i);
            int frequency = arr.get(wordIndex).get(i) == null ? 0 : arr.get(wordIndex).get(i);
            if (frequency > 0) {
                System.out.println(filename + "\t: " + frequency);
            }
        }
    }

    // Indexing method
    public static void Indexing(Hashtable<String, String> url_Map, String product) {
        InvertedIndexing<Integer> st = new InvertedIndexing<Integer>();
        st.tokenizeAndBuildIndex(st);

        int index = st.get(product);
        if (index != -1) {
            printFrequency(index);
        } else {
            System.out.println("Word not found in the index.");
        }
        System.out.println();
    }

    public static void main(String[] args) {

    }
}