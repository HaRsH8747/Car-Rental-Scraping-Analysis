package features;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageRanking {
    public static int appearNumber(String inputText, String srcText) {
        int count = 0;
        Pattern p = Pattern.compile(inputText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    public static String innerContent(String fileName, String folderName) throws IOException {

        File file = new File("../Car-Rental-Scraping-Analysis/" + folderName + "/" + fileName);

        FileInputStream fIStream;
        StringBuilder stringBuilder = new StringBuilder();
        fIStream = new FileInputStream(file);
        int temp = 0;
        while ((temp = fIStream.read()) != -1) {
            stringBuilder.append((char) temp);
        }
        return stringBuilder.toString();
    }

    public ArrayList<String> fileNameList(String folderName) {
        ArrayList<String> fNameList = new ArrayList<String>();
        String path = "../Car-Rental-Scraping-Analysis/" + folderName;
        File file = new File(path);

        File[] filelist = file.listFiles();

        for (File f : filelist) {
            fNameList.add(f.getName());

        }
        return fNameList;
    }

    public static void pageRank(String objWord) throws IOException {
        PageRanking pageRanking = new PageRanking();
        String[] folder = {"AvisFiles", "BudgetFiles", "OrbitzFiles"};

        HashMap<String, Integer> map = new HashMap<>();

//		System.out.print("Please enter your keywords: ");
//		Scanner inputWordScanner = new Scanner(System.in);
//		String objWord = inputWordScanner.next();

        // Loop through all the files that need to be detected
        // save the corresponding word frequency and file name in the HashMap
        for (String folderName : folder) {
            ArrayList<String> fNameList = new ArrayList<>();
            fNameList.addAll(pageRanking.fileNameList(folderName));

            for (String fNL : fNameList) {
                String fileText = innerContent(fNL, folderName);
                int NUMS = appearNumber(objWord.toLowerCase(), fileText.toLowerCase());
                map.put(fNL, NUMS);
            }
        }

        //PQ
        PriorityQueue<Entry<String, Integer>> hmPriorityQueue = new PriorityQueue<Entry<String, Integer>>(
                new Comparator<Entry<String, Integer>>() {
                    public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                        return e2.getValue() - e1.getValue();
                    }
                });

        for (Entry<String, Integer> entry : map.entrySet()) {
            hmPriorityQueue.add(entry);
        }

        //The number of loops determines the number of outputs
        System.out.println("Page Ranking based on the car name:");
        for (int p = 0; p < 10 && !hmPriorityQueue.isEmpty(); p++) {
            Entry<String, Integer> big = hmPriorityQueue.poll();
            if (big != null) {
                System.out.println((p+1)+". "+ big.getKey());
            }
        }

        System.out.println("Finished.");
    }

    public static void main(String[] args) throws IOException {
        PageRanking pageRanking = new PageRanking();
        String[] folder = { "AvisFiles", "BudgetFiles"};

        HashMap<String, Integer> map = new HashMap<>();

        System.out.print("Please enter your keywords: ");
        Scanner inputWordScanner = new Scanner(System.in);
        String objWord = inputWordScanner.next();

        // Loop through all the files that need to be detected
        // save the corresponding word frequency and file name in the HashMap
        for (String folderName : folder) {
            ArrayList<String> fNameList = new ArrayList<>();
            fNameList.addAll(pageRanking.fileNameList(folderName));

            for (String fNL : fNameList) {
                String fileText = innerContent(fNL, folderName);
                int NUMS = appearNumber(objWord.toLowerCase(), fileText.toLowerCase());
                map.put(fNL, NUMS);
            }
        }

        //PQ
        PriorityQueue<Entry<String, Integer>> hmPriorityQueue = new PriorityQueue<Entry<String, Integer>>(
                new Comparator<Entry<String, Integer>>() {
                    public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                        return e2.getValue() - e1.getValue();
                    }
                });

        for (Entry<String, Integer> entry : map.entrySet()) {
            hmPriorityQueue.add(entry);
        }

        //The number of loops determines the number of outputs
        for (int p = 0; p < 10 && !hmPriorityQueue.isEmpty(); p++) {
            Entry<String, Integer> big = hmPriorityQueue.poll();
            if (big != null) {
                System.out.println("The file name is " + big.getKey() + ", value is " + big.getValue());
            }
        }

        System.out.println("Finished.");
    }
}
